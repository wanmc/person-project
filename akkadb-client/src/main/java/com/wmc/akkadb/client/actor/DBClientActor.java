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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.Serializable;

import com.wmc.akkadb.commons.ConnectTimeoutException;
import com.wmc.akkadb.event.AbstractRequest;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorIdentity;
import akka.actor.ActorSelection;
import akka.actor.Identify;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
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
  private int lostBeatCount = 0;

  public DBClientActor(String dbUrl) {
    db = context().actorSelection("akka.tcp://Akka-db-system-server@" + dbUrl + "/user/db-actor");
    online = heartBeatBuilder().match(AbstractRequest.class, x -> {
      log.debug("向数据库发起请求：{}", x);
      db.forward(x, getContext());
    }).build();
  }

  @Override
  public void preStart() throws Exception {
    getContext().system().scheduler().schedule(Duration.create(100, MILLISECONDS),
        Duration.create(5000, MILLISECONDS), db.anchor(), new Identify(getClass().getSimpleName()),
        getContext().dispatcher(), self());
  }

  @Override
  public void postRestart(Throwable reason) throws Exception {
    log.error(reason.getMessage());
  }

  @Override
  public Receive createReceive() {
    return heartBeatBuilder().match(AbstractRequest.class, x -> {
      stash();
    }).build();
  }

  private ReceiveBuilder heartBeatBuilder() {
    return receiveBuilder().match(ActorIdentity.class,
        x -> !x.getActorRef().isPresent() && ++lostBeatCount >= 2, x -> {
          // 连接超时，抛出异常，由监控actor根据策略处理
          throw new ConnectTimeoutException("连续丢失2次心跳：{0}", db.anchorPath());
        }).match(ActorIdentity.class, x -> x.getActorRef().isPresent(), x -> {
          getContext().become(online);
          lostBeatCount = 0;
          unstashAll();
        });
  }

  static class HeartBeat implements Serializable {
    private static final long serialVersionUID = 5887397344090244996L;
  }
}
