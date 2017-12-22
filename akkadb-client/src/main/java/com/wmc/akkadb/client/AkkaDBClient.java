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

import com.wmc.akkadb.commons.KeyNotFoundException;
import com.wmc.akkadb.event.DeleteRequest;
import com.wmc.akkadb.event.GetRequest;
import com.wmc.akkadb.event.SetNXRequest;
import com.wmc.akkadb.event.SetRequest;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * @author Administrator
 *
 */
public class AkkaDBClient {
  private static final ActorSystem system = ActorSystem.create("Akka-db-system-client");

  private final ActorSelection selection;
  private long timeoutInMills = 500;

  public AkkaDBClient(String remoteUrl) {
    selection = system
        .actorSelection("akka.tcp://Akka-db-system-server@" + remoteUrl + "/user/db-actor");
  }

  public long getTimeoutInMills() {
    return timeoutInMills;
  }

  public void setTimeoutInMills(long timeoutInMills) {
    this.timeoutInMills = timeoutInMills;
  }

  public boolean set(String key, Object val) {
    SetRequest request = new SetRequest(key, val);
    Future future = ask(selection, request, timeoutInMills);
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
    SetRequest request = new SetRequest(key, val);
    return toJava(ask(selection, request, timeoutInMills));
  }

  public boolean setNX(String key, Object val) {
    SetNXRequest request = new SetNXRequest(key, val);
    Future future = ask(selection, request, timeoutInMills);
    boolean success = false;
    try {
      success = (boolean) Await.result(future, Duration.create(timeoutInMills, TimeUnit.SECONDS));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return success;
  }

  public CompletionStage asyncSetNX(String key, Object val) {
    SetNXRequest request = new SetNXRequest(key, val);
    return toJava(ask(selection, request, timeoutInMills));
  }

  public boolean delete(String key) {
    DeleteRequest request = new DeleteRequest(key);
    Future future = ask(selection, request, 500);
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
    return toJava(ask(selection, request, timeoutInMills));
  }

  public <T> T get(String key, Class<T> cls) {
    GetRequest request = new GetRequest(key);
    Future future = ask(selection, request, timeoutInMills);
    T result = null;
    try {
      result = (T) Await.result(future, Duration.create(timeoutInMills, TimeUnit.SECONDS));
    } catch (KeyNotFoundException e) {
      request = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public <T> CompletableFuture<T> asyncGet(String key, Class<T> cls) {
    GetRequest request = new GetRequest(key);
    Future<T> future = (Future<T>) ask(selection, request, timeoutInMills);
    return (CompletableFuture<T>)toJava(future);
  }
}
