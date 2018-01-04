/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	ClusterNode.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月30日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.api;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

/**
 * @author Administrator
 *
 */
public class ClusterNode {

  private final ActorSelection actor;
  private boolean reachable = true;

  public ClusterNode(ActorSelection actor) {
    this.actor = actor;
  }

  public void unreachable() {
    reachable = false;
  }

  public void tell(Object msg, ActorRef sender) {
    if (reachable)
      actor.tell(msg, sender);
  }
}
