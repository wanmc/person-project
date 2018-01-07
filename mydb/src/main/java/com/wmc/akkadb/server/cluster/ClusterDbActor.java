/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	ClusterDbActor.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月6日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.alibaba.fastjson.JSONArray;
import com.wmc.akkadb.commons.KeyNotFoundException;
import com.wmc.akkadb.event.AbstractRequest;
import com.wmc.akkadb.event.DeleteRequest;
import com.wmc.akkadb.event.GetRequest;
import com.wmc.akkadb.event.SetNXRequest;
import com.wmc.akkadb.event.SetRequest;
import com.wmc.akkadb.server.api.Data;
import com.wmc.akkadb.server.api.PublishRequest;
import com.wmc.akkadb.server.paxos.Acceptor;
import com.wmc.akkadb.server.paxos.Learner;
import com.wmc.akkadb.server.paxos.Paxos;
import com.wmc.akkadb.server.paxos.PaxosKey;
import com.wmc.akkadb.server.paxos.Prepare;
import com.wmc.akkadb.server.paxos.Promise;
import com.wmc.akkadb.server.paxos.Proposal;
import com.wmc.akkadb.server.paxos.Proposer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.actor.Status.Failure;
import akka.cluster.pubsub.DistributedPubSubMediator.Publish;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;

/**
 * @author wanmc
 *
 */
public class ClusterDbActor extends ClusterActor implements Acceptor, Learner {

  private final Map<String, Data> db = new HashMap<>();
  // 副本数量（包括自己，如果由3个节点构成一个小集群，则本数值为3）
  private int replicas = 3;
  private int timeout = 1500;

  @Override
  public Receive createReceive() {
    return clusterBuilder()//
        .match(AbstractRequest.class, this::publishSharding)//
        .match(PublishRequest.class, this::handle)//
        .match(Prepare.class, this::prepareResponse)//
        .match(Proposal.class, this::acceptResponse).build();
  }

  private String getSharding(AbstractRequest request) {
    final int shardingIdx = request.getKey().hashCode() % shardingCount;
    return getSharding(shardingIdx);
  }

  /** 在分片内广播 */
  private void publishSharding(AbstractRequest request) {
    String sharding = getSharding(request);
    ActorRef extraActor = buildExtraActor(getSender(), request, sharding);
    mediator.tell(new Publish(sharding, new PublishRequest(request)), extraActor);
    log.debug("广播：{}", request);
    context().system().scheduler().scheduleOnce(Duration.create(timeout, TimeUnit.MILLISECONDS),
        extraActor, "timeout", context().dispatcher(), self());
  }

  private ActorRef buildExtraActor(final ActorRef senderRef, AbstractRequest request,
      final String sharding) {
    if (request instanceof GetRequest) {
      return context().system()
          .actorOf(Props.create(TempActor.class, () -> new TempActor(senderRef)));
    } else {
      return context().system().actorOf(
          Props.create(TempPaxosActor.class, () -> new TempPaxosActor(senderRef, sharding)));
    }
  }

  private void handle(PublishRequest inner) {
    AbstractRequest request = inner.getRequest();
    if (request instanceof GetRequest) {
      sender().tell(select(request.getKey()), getSelf());
    } else {
      // 将当前节点paxos实例返回给propser，由propser筛选出最新的paxos实例（由于需要多数派返回结果，所以一定可以得到最新的实例）
      PaxosKey paxosKey = createPaxosKey(request);
      int promiseN = getPromised(paxosKey);
      Paxos paxos = new Paxos(paxosKey.getPaxosId(), promiseN, request);
      sender().tell(paxos, getSelf());
    }
  }

  private Map<PaxosKey, Integer> promiseds = new HashMap<>();
  private Map<PaxosKey, Proposal> accepteds = new HashMap<>();

  /** 生成request的key对应的当前paxos实例的key */
  public PaxosKey createPaxosKey(AbstractRequest request) {
    final String key = request.getKey();
    Data data = db.get(key);
    if (data == null) {
      return new PaxosKey(key, 1);
    } else {
      return new PaxosKey(key, data.getPaxosId() + 1);
    }
  }

  @Override
  public int getPromised(PaxosKey key) {
    return promiseds.get(key) == null ? 0 : promiseds.get(key);
  }

  @Override
  public void promise(PaxosKey key, int n) {
    promiseds.put(key, n);
  }

  @Override
  public void prepareResponse(Prepare prepare) {
    final PaxosKey paxosKey = prepare.getPaxosKey();
    final int N = prepare.getN();
    final int promised = getPromised(paxosKey);
    if (N > promised) {
      promise(paxosKey, N);
      Proposal accepted = getAccepted(paxosKey);
      int acceptedN = accepted == null ? 0 : accepted.getN();
      AbstractRequest acceptedV = accepted == null ? null : accepted.getVal();
      sender().tell(new Promise(acceptedN, acceptedV), getSelf());
    } else {
      // TODO 已接收了大于编号N的提案，拒绝编号N
    }
  }

  @Override
  public Proposal getAccepted(PaxosKey key) {
    return accepteds.get(key);
  }

  @Override
  public void accept(PaxosKey key, Proposal proposal) {
    accepteds.put(key, proposal);
  }

  @Override
  public void acceptResponse(Proposal proposal) {
    final PaxosKey key = proposal.getPaxosKey();
    final int N = proposal.getN();
    if (N >= getPromised(key)) {
      accept(key, proposal);
      Serializable learn = learn(proposal);
      sender().tell(new Data(key.getPaxosId(), learn), getSelf());
      clearTemp(key);
    } else {
      // TODO 拒绝
    }
  }

  @Override
  public Serializable learn(Proposal chosen) {
    long paxosId = chosen.getPaxosId();
    final AbstractRequest request = chosen.getVal();
    final String key = request.getKey();
    if (request instanceof SetRequest) {
      db.put(key, new Data(paxosId, ((SetRequest) request).getVal()));
      return true;
    } else if (request instanceof SetNXRequest) {
      if (db.get(key) == null) {
        db.put(key, new Data(paxosId, ((SetRequest) request).getVal()));
        return true;
      }
      return false;
    } else if (request instanceof DeleteRequest) {
      db.remove(key);
      return true;
    } else {
      return new Failure(new RuntimeException());
    }
  }

  private void clearTemp(PaxosKey key) {
    // TODO 清除小于key的tmp
    promiseds.remove(key);
    accepteds.remove(key);
  }

  private Data select(String key) {
    Data r = db.get(key);
    if (r == null) {
      return new Data(0, new Status.Failure(new KeyNotFoundException(key)));
    } else if (r.getVal() == null) {
      return new Data(r.getPaxosId(), new Status.Failure(new KeyNotFoundException(key)));
    } else {
      return r;
    }
  }

  class TempActor extends AbstractActor {
    protected final List<Data> temp = new ArrayList<>();
    protected final ActorRef senderRef;

    public TempActor(ActorRef sender) {
      this.senderRef = sender;
    }

    @Override
    public Receive createReceive() {
      return builder().build();
    }

    public ReceiveBuilder builder() {
      return receiveBuilder().match(String.class, x -> x.equals("timeout"), x -> {
        senderRef.tell(new Failure(new TimeoutException()), self());
        context().stop(self());
      }).match(Data.class, r -> {
        temp.add(r);
        if (temp.size() > replicas / 2) {
          Data max = Collections.max(temp, (x, y) -> {
            return x.getPaxosId().compareTo(y.getPaxosId());
          });
          senderRef.tell(max.getVal(), ActorRef.noSender());
          context().stop(getSelf());
        }
      });
    }
  }

  class TempPaxosActor extends TempActor implements Proposer {
    private final String sharding;
    private final List<Paxos> tmpPaxos = new ArrayList<>(replicas);
    private final List<Promise> tmpPromise = new ArrayList<>(replicas);

    private long paxosId;
    private int N;
    private AbstractRequest request;

    public TempPaxosActor(ActorRef sender, String sharding) {
      super(sender);
      this.sharding = sharding;
    }

    @Override
    public Receive createReceive() {
      return builder()//
          .match(Paxos.class, paxos -> {
            tmpPaxos.add(paxos);
            log.debug(JSONArray.toJSONString(tmpPaxos));
            if (tmpPaxos.size() > replicas / 2) {
              prepareRequest();
            }
          })//
          .match(Promise.class, promise -> {
            tmpPromise.add(promise);
            log.debug(JSONArray.toJSONString(tmpPromise));
            if (tmpPromise.size() > replicas / 2) {
              acceptRequest();
            }
          }).build();
    }

    @Override
    public void prepareRequest() {
      Paxos max = Collections.max(tmpPaxos, (x, y) -> {
        return x.getId().compareTo(y.getId());
      });
      this.paxosId = max.getId();
      this.N = max.getPromiseN() + 1;
      this.request = max.getRequest();
      Prepare prepare = new Prepare(paxosId, request.getKey(), N);
      mediator.tell(new Publish(sharding, prepare), getSelf());
    }

    @Override
    public void acceptRequest() {
      Promise max = Collections.max(tmpPromise, (x, y) -> {
        return x.getAcceptedN().compareTo(y.getAcceptedN());
      });
      if (max.getAcceptedV() == null) {
        // acceptors没有接受过议案，proposer可以自行决定议案的V
        Proposal proposal = new Proposal(paxosId, N, request);
        mediator.tell(new Publish(sharding, proposal), getSelf());
      } else if (max.getAcceptedV().equals(request)) {
        // acceptors已接受过议案，proposer只能提出响应中N最大的提案的V
        Proposal proposal = new Proposal(paxosId, N, max.getAcceptedV());
        mediator.tell(new Publish(sharding, proposal), getSelf());
      } else {
        // TODO 当前paxos已经chosen，应该终止提交议案
        senderRef.tell(false, ActorRef.noSender());
        context().stop(getSelf());
      }
    }
  }
}
