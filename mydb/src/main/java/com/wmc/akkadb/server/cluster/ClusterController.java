/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	ClusterActor.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月29日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.wmc.akkadb.event.AbstractRequest;
import com.wmc.akkadb.event.StringRequest;
import com.wmc.akkadb.server.ConfigLoader;
import com.wmc.akkadb.server.api.ClusterNode;
import com.wmc.akkadb.server.api.TimestampRequest;
import com.wmc.akkadb.server.api.TimestampResponse;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author Administrator
 *
 */
public class ClusterController extends AbstractActor {
  private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  Cluster cluster = Cluster.get(getContext().system());
  private final List<ClusterNode> nodes = new ArrayList<>();
  private final Map<Long, List<TimestampResponse>> temp = new HashMap<>();
  private final Map<Long, ActorRef> tempSender = new HashMap<>();

  @Override
  public void preStart() throws Exception {
    cluster.subscribe(getSelf(), MemberEvent.class, UnreachableMember.class);
  }

  @Override
  public void postStop() throws Exception {
    cluster.leave(self().path().address());
    cluster.unsubscribe(getSelf());
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(MemberEvent.class, x -> x.member().status() == MemberStatus.up(), x -> {
          ActorSelection actor = context().actorSelection(x.member().address() + "/user/"
              + ConfigLoader.get().getString("akka.actor.server_name"));
          nodes.add(new ClusterNode(actor));
          log.info("集群节点[{}]加入", x);
        }).match(UnreachableMember.class, x -> {
          // TODO
          log.info("集群节点[{}]访问不可到达", x);
        }).match(AbstractRequest.class, x -> {
          long timestamp = System.currentTimeMillis();
          temp.put(timestamp, new ArrayList<>());
          tempSender.put(timestamp, sender());
          tell(timestamp, x);
        }).match(TimestampResponse.class, x -> {
          answer(x);
        }).match(String.class, x -> x.equals(StringRequest.CONNECT), x -> {
          ActorRef sender = sender();
          log.info("客户端[{}]连接成功！", sender.path());
          sender.tell(StringRequest.CONNECTED, self());
        }).build();
  }

  private void tell(long timestamp, AbstractRequest x) {
    int cur = x.getKey().hashCode() % nodes.size();
    TimestampRequest request = new TimestampRequest(x, timestamp);
    nodes.get(cur).tell(request, getSelf());
    prevNode(cur).tell(request, getSelf());
    nextNode(cur).tell(request, getSelf());
  }

  private void answer(TimestampResponse res) {
    long ts = res.getTimestamp();
    ActorRef sender = tempSender.get(ts);
    if (sender == null)
      return;
    List<TimestampResponse> results = temp.get(ts);
    results.add(res);
    if (results.size() >= 3) {
      Map<TimestampResponse, Long> collect = results.stream()
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      TimestampResponse result = null;
      for (TimestampResponse k : collect.keySet()) {
        if (result == null && collect.get(k) > 1) {
          result = k;
        } else if (collect.get(k) > collect.get(result)) {
          result = k;
        }
      }
      if (result != null)
        sender.tell(res.getResponse(), self());
    }

    // TODO 保证最终一致性问题
  }

  private ClusterNode prevNode(int cur) {
    if (--cur < 0)
      return nodes.get(nodes.size() - 1);
    else
      return nodes.get(cur);
  }

  private ClusterNode nextNode(int cur) {
    if (++cur < nodes.size())
      return nodes.get(cur);
    else
      return nodes.get(0);
  }
}
