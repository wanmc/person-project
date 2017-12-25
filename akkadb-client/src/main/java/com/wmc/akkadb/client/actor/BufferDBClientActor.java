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

import com.wmc.akkadb.client.actor.BufferDBClientActor.State;
import com.wmc.akkadb.commons.ConnectTimeoutException;
import com.wmc.akkadb.event.AbstractRequest;
import com.wmc.akkadb.event.FlushEvent;
import com.wmc.akkadb.event.RequestMap;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

/**
 * 带缓冲区的客户端actor，负责与数据库之间的交互 <br>
 * 会将请求合并，一起发送到数据库服务端
 * 
 * @author wanmc
 */
public class BufferDBClientActor extends AbstractFSM<State, RequestMap> {
  private static final int DEFAULT_THRESHOLD = 200;
  private final LoggingAdapter log = Logging.getLogger(context().system(), this);
  private final ActorSelection db;
  private long threshold = DEFAULT_THRESHOLD; // buffer时间的阈值

  public BufferDBClientActor(String dbUrl) {
    db = context().actorSelection("akka.tcp://Akka-db-system-server@" + dbUrl + "/user/db-actor");
  }

  public BufferDBClientActor(String dbUrl, int threshold) {
    db = context().actorSelection("akka.tcp://Akka-db-system-server@" + dbUrl + "/user/db-actor");
    this.threshold = threshold;
  }

  @Override
  public void preStart() throws Exception {
    log.info("数据库连接actor启动");
    startWith(State.DISCONNECTED, new RequestMap());

    when(State.DISCONNECTED, matchEvent(FlushEvent.class, (e, container) -> stay()) //
        .event(AbstractRequest.class, (e, container) -> {
          // 向db发起连接请求
          db.tell(CONNECT, self());
          container.put(e, sender());
          return stay();
        }).event((e, container) -> e instanceof String && e.equals(CONNECTED), (e, container) -> {
          // 如果收到连接成功消息，将状态迁移到“已连接”
          if (container.isEmpty()) {
            return goTo(State.CONNECTED);
          } else {
            durationFlush();
            return goTo(State.CONNECTED_AND_PENDING);
          }
        })
        .event((e, container) -> e instanceof String && e.equals(CONNECT_CHECK), (e, container) -> {
          // 连接超时，抛出异常，由监控actor根据策略处理
          throw new ConnectTimeoutException();
        }));
    when(State.CONNECTED, matchEvent(FlushEvent.class, (e, container) -> stay()) //
        .event(AbstractRequest.class, (e, container) -> {
          container.put(e, sender());
          durationFlush();
          return goTo(State.CONNECTED_AND_PENDING);
        }));
    when(State.CONNECTED_AND_PENDING, matchEvent(FlushEvent.class, (e, container) -> {
      System.out.println("收到flush消息, 将发送buffer中的消息" + container.toString());
      RequestMap dest = new RequestMap();
      container.forEach((k, v) -> {
        dest.put(k, v);
      });
      db.tell(dest, self());
      container.clear();
      return goTo(State.CONNECTED);
    }).event(AbstractRequest.class, (e, container) -> {
      container.put(e, sender());
      return stay();
    }));

    initialize();

    // 检查连接超时
    context().system().scheduler().scheduleOnce(Duration.create(5, TimeUnit.SECONDS), self(),
        CONNECT_CHECK, context().dispatcher(), ActorRef.noSender());
  }

  private void durationFlush() {
    context().system().scheduler().scheduleOnce(Duration.create(threshold, TimeUnit.MILLISECONDS),
        self(), new FlushEvent(), context().dispatcher(), ActorRef.noSender());
  }

  static enum State {
    DISCONNECTED, CONNECTED, CONNECTED_AND_PENDING
  }
}
