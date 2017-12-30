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

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author Administrator
 *
 */
public class ClusterController extends AbstractActor {
  private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  Cluster cluster = Cluster.get(getContext().system());

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
    // TODO Auto-generated method stub
    return receiveBuilder().match(MemberEvent.class, x -> {
      log.info("集群节点[{}]信息", x);
    }).match(UnreachableMember.class, x -> {
      log.info("集群节点[{}]访问不可到达", x);
    }).build();
  }
}
