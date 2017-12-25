/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-client
 * 文件名：	DBClientActor.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月24日 - wanmc - 创建。
 */
package com.wmc.akkadb.client.actor;

import static com.wmc.akkadb.event.StringRequest.CONNECT;
import static com.wmc.akkadb.event.StringRequest.CONNECTED;
import static com.wmc.akkadb.event.StringRequest.CONNECT_CHECK;

import java.util.concurrent.TimeUnit;

import com.wmc.akkadb.commons.ConnectTimeoutException;
import com.wmc.akkadb.event.AbstractRequest;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

/**
 * 客户端actor，负责与数据库之间的交互
 * 
 * @author wanmc
 */
public class DBClientActor extends AbstractActorWithStash {
  private final LoggingAdapter log = Logging.getLogger(context().system(), this);
  private final ActorSelection db;
  private final Receive online;

  public DBClientActor(String dbUrl) {
    db = context().actorSelection("akka.tcp://Akka-db-system-server@" + dbUrl + "/user/db-actor");
    online = receiveBuilder().match(AbstractRequest.class, x -> {
      log.debug("向数据库发起请求：{}", x);
      db.forward(x, getContext());
    }).build();
  }

  @Override
  public void preStart() throws Exception {
    log.info("数据库连接actor启动");
    context().system().scheduler().scheduleOnce(Duration.create(5, TimeUnit.SECONDS), self(),
        CONNECT_CHECK, context().dispatcher(), ActorRef.noSender());
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(AbstractRequest.class, x -> {
      System.out.println(x);
      log.debug("连接数据库...");
      db.tell(CONNECT, self());
      stash();
    }).match(String.class, x -> x.equals(CONNECTED), x -> {
      log.debug("连接成功！");
      getContext().become(online);
      unstashAll();
    }).match(String.class, x -> x.equals(CONNECT_CHECK), x -> {
      // 连接超时，抛出异常，由监控actor根据策略处理
      throw new ConnectTimeoutException();
    }).build();
  }
}
