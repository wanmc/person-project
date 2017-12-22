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

import java.util.concurrent.TimeUnit;

import com.wmc.akkadb.commons.KeyNotFoundException;
import com.wmc.akkadb.event.DeleteRequest;
import com.wmc.akkadb.event.GetRequest;
import com.wmc.akkadb.event.SetNXRequest;
import com.wmc.akkadb.event.SetRequest;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
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

  public AkkaDBClient(String remoteUrl) {
    selection = system
        .actorSelection("akka.tcp://Akka-db-system-server@" + remoteUrl + "/user/db-actor");
  }

  public boolean set(String key, Object val) {
    SetRequest request = new SetRequest(key, val);
    Future future = Patterns.ask(selection, request, 500);
    boolean success = false;
    try {
      success = (boolean) Await.result(future, Duration.create(1, TimeUnit.SECONDS));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return success;
  }

  public boolean setNX(String key, Object val) {
    SetNXRequest request = new SetNXRequest(key, val);
    Future future = Patterns.ask(selection, request, 500);
    boolean success = false;
    try {
      success = (boolean) Await.result(future, Duration.create(1, TimeUnit.SECONDS));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return success;
  }

  public boolean delete(String key) {
    DeleteRequest request = new DeleteRequest(key);
    Future future = Patterns.ask(selection, request, 500);
    boolean success = false;
    try {
      success = (boolean) Await.result(future, Duration.create(1, TimeUnit.SECONDS));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return success;
  }

  public <T> T get(String key, Class<T> cls) {
    GetRequest request = new GetRequest(key);
    Future future = Patterns.ask(selection, request, 500);
    T result = null;
    try {
      result = (T) Await.result(future, Duration.create(1, TimeUnit.SECONDS));
    } catch (KeyNotFoundException e) {
      request = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
