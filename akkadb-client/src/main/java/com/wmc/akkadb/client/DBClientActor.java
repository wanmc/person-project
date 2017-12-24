/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-client
 * 文件名：	DBClientActor.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月24日 - wanmc - 创建。
 */
package com.wmc.akkadb.client;

import com.wmc.akkadb.event.AbstractRequest;
import com.wmc.akkadb.event.Connected;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

/**
 * @author wanmc
 *
 */
public class DBClientActor extends AbstractActorWithStash {
  private final LoggingAdapter log = Logging.getLogger(context().system(), this);
  private final ActorSelection db;
  private final PartialFunction<Object, BoxedUnit> online;

  public DBClientActor(String dbUrl) {
    db = context().actorSelection("akka.tcp://Akka-db-system-server@" + dbUrl + "/user/db-actor");
    online = receiveBuilder().match(AbstractRequest.class, x -> {
      log.debug("向数据库发起请求：{}", x);
      db.forward(x, getContext());
    }).match(Connected.class, x -> {
      log.debug("并发连接请求，将stash中的消息回复");
      unstash();
    }).build().onMessage();
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(AbstractRequest.class, x -> {
      System.out.println(x);
      log.debug("连接数据库...");
      db.tell(new Connected(), self());
      stash();
    }).match(Connected.class, x -> {
      log.debug("连接成功！");
      context().become(online);
      unstash();
    }).build();
  }
}
