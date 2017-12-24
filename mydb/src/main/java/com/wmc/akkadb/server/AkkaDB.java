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
import com.wmc.akkadb.event.DeleteRequest;
import com.wmc.akkadb.event.GetRequest;
import com.wmc.akkadb.event.RequestQueue;
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
    return receiveBuilder()//
        .match(GetRequest.class, e -> get(e, true))//
        .match(SetRequest.class, e -> set(e, true))//
        .match(SetNXRequest.class, e -> setNX(e, true))//
        .match(DeleteRequest.class, e -> delete(e, true))//
        .match(RequestQueue.class, queue -> {
          queue.forEach(e -> {
            if (e instanceof GetRequest) {
              get((GetRequest) e);
            } else if (e instanceof SetRequest) {
              set((SetRequest) e);
            } else if (e instanceof SetNXRequest) {
              setNX((SetNXRequest) e);
            } else if (e instanceof DeleteRequest) {
              delete((DeleteRequest) e);
            }
            answer(true, true);
          });
        }).match(String.class, x -> x.equals("connect"), x -> {
          ActorRef sender = sender();
          log.info("客户端[{}]连接成功！", sender.path());
          sender.tell("connected", self());
        }).matchAny(e -> {
          sender().tell(new Status.Failure(new IllegalRequestException("未知的事件：{0}", e)), self());
        }).build();
  }

  private void get(GetRequest e) {
    get(e, false);
  }

  private void set(SetRequest e) {
    set(e, false);
  }

  private void setNX(SetNXRequest e) {
    setNX(e, false);
  }

  private void delete(DeleteRequest e) {
    delete(e, false);
  }

  private void get(GetRequest e, boolean answer) {
    log.debug("receive set request: {}", e);
    answer(get(e.getKey()), answer);
  }

  private void set(SetRequest e, boolean answer) {
    log.debug("receive set request: {}", e);
    map.put(e.getKey(), e.getVal());
    answer(true, answer);
  }

  private void setNX(SetNXRequest e, boolean answer) {
    log.debug("receive set request: {}", e);
    String key = e.getKey();
    if (map.get(key) == null) {
      map.put(e.getKey(), e.getVal());
      answer(true, answer);
    } else {
      answer(false, answer);
    }
  }

  private void delete(DeleteRequest e, boolean answer) {
    log.debug("receive delete request: {}", e);
    map.remove(e.getKey());
    answer(true, answer);
  }

  private void answer(Object msg, boolean answer) {
    if (answer) {
      sender().tell(msg, self());
    }
  }

  private Object get(String key) {
    Object r = map.get(key);
    return r == null ? new Status.Failure(new KeyNotFoundException(key)) : r;
  }
}
