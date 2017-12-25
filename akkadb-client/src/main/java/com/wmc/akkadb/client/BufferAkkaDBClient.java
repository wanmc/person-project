/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-client
 * 文件名：	AkkaDBClient.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月22日 - Administrator - 创建。
 */
package com.wmc.akkadb.client;

import static akka.pattern.Patterns.ask;
import static scala.compat.java8.FutureConverters.toJava;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.wmc.akkadb.client.actor.BufferDBClientActor;
import com.wmc.akkadb.commons.KeyNotFoundException;
import com.wmc.akkadb.event.AbstractRequest;
import com.wmc.akkadb.event.DeleteRequest;
import com.wmc.akkadb.event.GetRequest;
import com.wmc.akkadb.event.SetNXRequest;
import com.wmc.akkadb.event.SetRequest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * @author Administrator
 *
 */
public class BufferAkkaDBClient {
  private final ActorRef actor;
  private long timeoutInMills;

  public BufferAkkaDBClient(ActorSystem system, String remoteUrl) {
    this(system, remoteUrl, 500L);
  }

  public BufferAkkaDBClient(ActorSystem system, String remoteUrl, long timeoutInMills) {
    this.timeoutInMills = timeoutInMills;
    actor = system.actorOf(Props.create(BufferDBClientActor.class, remoteUrl));
  }

  public long getTimeoutInMills() {
    return timeoutInMills;
  }

  public void setTimeoutInMills(long timeoutInMills) {
    this.timeoutInMills = timeoutInMills;
  }

  public boolean set(String key, Object val) {
    if (val.getClass().getClassLoader() != null) {
      val = JSON.toJSONString(val);
    }
    SetRequest request = new SetRequest(key, val);
    Future future = ask(actor, request, timeoutInMills);
    boolean success = false;
    try {
      success = (boolean) Await.result(future,
          Duration.create(timeoutInMills, TimeUnit.MILLISECONDS));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return success;
  }

  public CompletionStage asyncSet(String key, Object val) {
    if (val.getClass().getClassLoader() != null) {
      val = JSON.toJSONString(val);
    }
    SetRequest request = new SetRequest(key, val);
    return toJava(ask(actor, request, timeoutInMills));
  }

  public boolean setNX(String key, Object val) {
    if (val.getClass().getClassLoader() != null) {
      val = JSON.toJSONString(val);
    }
    SetNXRequest request = new SetNXRequest(key, val);
    Future future = ask(actor, request, timeoutInMills);
    boolean success = false;
    try {
      success = (boolean) Await.result(future, Duration.create(timeoutInMills, TimeUnit.SECONDS));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return success;
  }

  public CompletionStage asyncSetNX(String key, Object val) {
    if (val.getClass().getClassLoader() != null) {
      val = JSON.toJSONString(val);
    }
    SetNXRequest request = new SetNXRequest(key, val);
    return toJava(ask(actor, request, timeoutInMills));
  }

  public boolean delete(String key) {
    DeleteRequest request = new DeleteRequest(key);
    Future future = ask(actor, request, 500);
    boolean success = false;
    try {
      success = (boolean) Await.result(future, Duration.create(timeoutInMills, TimeUnit.SECONDS));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return success;
  }

  public CompletionStage asyncDelete(String key) {
    DeleteRequest request = new DeleteRequest(key);
    return toJava(ask(actor, request, timeoutInMills));
  }

  public <T> T get(String key, Class<T> cls) {
    GetRequest request = new GetRequest(key);
    Future future = ask(actor, request, timeoutInMills);
    T result = null;
    try {
      Object obj = Await.result(future, Duration.create(timeoutInMills, TimeUnit.SECONDS));
      if (cls.getClassLoader() == null)
        result = (T) obj;
      else
        result = JSON.parseObject(obj.toString(), cls);
    } catch (KeyNotFoundException e) {
      request = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public <T> CompletableFuture<T> asyncGet(String key, Class<T> cls) {
    GetRequest request = new GetRequest(key);
    Future future = ask(actor, request, timeoutInMills);
    CompletionStage result = toJava(future).thenApply(x -> {
      if (x != null && x instanceof String && cls.getClassLoader() != null) {
        return JSON.parseObject(x.toString(), cls);
      }
      return x;
    });
    return (CompletableFuture<T>) result;
  }

  public void tell(AbstractRequest request, ActorRef sender) {
    actor.tell(request, sender);
  }
}
