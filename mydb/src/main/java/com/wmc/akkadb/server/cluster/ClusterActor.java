/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	ClusterActor.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月29日 - wanmc - 创建。
 */
package com.wmc.akkadb.server.cluster;

import com.typesafe.config.ConfigFactory;
import com.wmc.akkadb.server.ConfigLoader;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.MemberStatus;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

/**
 * @author wanmc
 */
public abstract class ClusterActor extends AbstractActor {
  protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  private Cluster cluster = Cluster.get(getContext().system());
  protected final ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
  protected final int shardingCount = ConfigLoader.get().getInt("wmc.sharding.count");

  @Override
  public void preStart() throws Exception {
    cluster.subscribe(getSelf(), MemberEvent.class, UnreachableMember.class);
    mediator.tell(new DistributedPubSubMediator.Subscribe(getSharding(), getSelf()), getSelf());
  }

  @Override
  public void postStop() throws Exception {
    cluster.leave(self().path().address());
    cluster.unsubscribe(getSelf());
  }

  public ReceiveBuilder clusterBuilder() {
    return receiveBuilder()
        .match(MemberEvent.class, x -> x.member().status() == MemberStatus.up(), x -> {
          log.info("集群节点[{}]加入", x);
        }).match(MemberEvent.class, x -> x.member().status() == MemberStatus.down(), x -> {
          log.info("集群节点[{}]离开", x);
        }).match(UnreachableMember.class, x -> {
          log.info("集群节点[{}]访问不可到达", x);
        }).match(DistributedPubSubMediator.SubscribeAck.class, msg -> {
          log.info("分片集群: " + msg.subscribe().topic());
        });
  }

  public String getSharding() {
    return ConfigFactory.systemProperties().getString("wmc.sharding.name");
  }

  public static String getSharding(int index) {
    String str = ConfigFactory.systemProperties().getString("wmc.sharding.name");
    return str.substring(0, str.lastIndexOf("_") + 1) + index;
  }
}
