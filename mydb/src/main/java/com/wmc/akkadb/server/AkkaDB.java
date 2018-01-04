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
import com.wmc.akkadb.event.AbstractRequest;
import com.wmc.akkadb.event.DeleteRequest;
import com.wmc.akkadb.event.GetRequest;
import com.wmc.akkadb.event.RequestMap;
import com.wmc.akkadb.event.SetNXRequest;
import com.wmc.akkadb.event.SetQueue;
import com.wmc.akkadb.event.SetRequest;
import com.wmc.akkadb.server.api.TimestampRequest;
import com.wmc.akkadb.server.api.TimestampResponse;

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
        .match(GetRequest.class, e -> get(e))//
        .match(SetRequest.class, e -> set(e))//
        .match(SetNXRequest.class, e -> setNX(e))//
        .match(DeleteRequest.class, e -> delete(e))//
        .match(TimestampRequest.class, e -> {
          sender().tell(new TimestampResponse(self(), handle(e.getRequest()), e.getTimestamp()),
              self());
        }).match(SetQueue.class, queue -> {
          queue.forEach(e -> {
            set(e);
          });
          answer(true, true);
        }).match(RequestMap.class, map -> {
          map.forEach((e, sender) -> {
            answer(handle(e), sender);
          });
        }).matchAny(e -> {
          sender().tell(new Status.Failure(new IllegalRequestException("未知的事件：{0}", e)), self());
        }).build();
  }

  private Object handle(AbstractRequest e) {
    if (e instanceof GetRequest) {
      return get((GetRequest) e, false);
    } else if (e instanceof SetRequest) {
      return set((SetRequest) e, false);
    } else if (e instanceof SetNXRequest) {
      return setNX((SetNXRequest) e, false);
    } else if (e instanceof DeleteRequest) {
      return delete((DeleteRequest) e, false);
    }
    return new Status.Failure(new IllegalRequestException("未知的事件：{0}", e));
  }

  private void get(GetRequest e) {
    get(e, true);
  }

  private void set(SetRequest e) {
    set(e, true);
  }

  private void setNX(SetNXRequest e) {
    setNX(e, true);
  }

  private void delete(DeleteRequest e) {
    delete(e, true);
  }

  private Object get(GetRequest e, boolean answer) {
    log.debug("receive get request: {}", e);
    return answer(get(e.getKey()), answer);
  }

  private Object set(SetRequest e, boolean answer) {
    log.debug("receive set request: {}", e);
    map.put(e.getKey(), e.getVal());
    return answer(true, answer);
  }

  private Object setNX(SetNXRequest e, boolean answer) {
    log.debug("receive setNX request: {}", e);
    String key = e.getKey();
    if (map.get(key) == null) {
      map.put(e.getKey(), e.getVal());
      return answer(true, answer);
    } else {
      return answer(false, answer);
    }
  }

  private Object delete(DeleteRequest e, boolean answer) {
    log.debug("receive delete request: {}", e);
    map.remove(e.getKey());
    return answer(true, answer);
  }

  private Object answer(Object msg, boolean answer) {
    if (answer) {
      answer(msg, sender());
    }
    return msg;
  }

  private void answer(Object msg, ActorRef sender) {
    sender.tell(msg, self());
  }

  private Object get(String key) {
    Object r = map.get(key);
    return r == null ? new Status.Failure(new KeyNotFoundException(key)) : r;
  }
}
