/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	AkkaDB.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月20日 - wanmc - 创建。
 */
package com.wmc.akkadb.server;

import java.util.HashMap;
import java.util.Map;

import com.wmc.akkadb.commons.IllegalRequestException;
import com.wmc.akkadb.commons.KeyNotFoundException;
import com.wmc.akkadb.event.Connected;
import com.wmc.akkadb.event.DeleteRequest;
import com.wmc.akkadb.event.GetRequest;
import com.wmc.akkadb.event.SetNXRequest;
import com.wmc.akkadb.event.SetRequest;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author wanmc
 *
 */
public class AkkaDB extends AbstractActor {
  protected final LoggingAdapter log = Logging.getLogger(context().system(), this);

  public final Map<String, Object> map = new HashMap<>();

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(SetNXRequest.class, e -> {
      log.debug("receive set request: {}", e);
      String key = e.getKey();
      if (map.get(key) == null) {
        map.put(e.getKey(), e.getVal());
        sender().tell(true, self());
      } else {
        sender().tell(false, self());
      }
    }).match(GetRequest.class, e -> {
      log.debug("receive set request: {}", e);
      sender().tell(get(e.getKey()), self());
    }).match(SetRequest.class, e -> {
      log.debug("receive set request: {}", e);
      map.put(e.getKey(), e.getVal());
      sender().tell(true, self());
    }).match(DeleteRequest.class, e -> {
      log.debug("receive delete request: {}", e);
      map.remove(e.getKey());
      sender().tell(true, self());
    }).match(Connected.class, x -> {
      ActorRef sender = sender();
      log.info("客户端[{}]连接成功！", sender.path());
      sender.tell(x, self());
    }).matchAny(e -> {
      sender().tell(new Status.Failure(new IllegalRequestException("未知的事件：{0}", e)), self());
    }).build();
  }

  private Object get(String key) {
    Object r = map.get(key);
    return r == null ? new Status.Failure(new KeyNotFoundException(key)) : r;
  }
}
