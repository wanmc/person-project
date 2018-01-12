///**
// * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
// * 
// * 项目名：	mydb
// * 文件名：	AkkamyDBTest.java
// * 模块说明：	
// * 修改历史：
// * 2017年12月20日 - Administrator - 创建。
// */
//package com.wmc.akkadb.test;
//
//import static akka.actor.ActorRef.noSender;
//import static org.junit.Assert.assertEquals;
//
//import java.text.MessageFormat;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionStage;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//
//import org.junit.Test;
//
//import com.wmc.akkadb.event.GetRequest;
//import com.wmc.akkadb.event.RequestMap;
//import com.wmc.akkadb.event.SetNXRequest;
//import com.wmc.akkadb.event.SetQueue;
//import com.wmc.akkadb.event.SetRequest;
//import com.wmc.akkadb.server.AkkaDB;
//
//import akka.actor.AbstractActor;
//import akka.actor.ActorSystem;
//import akka.actor.Props;
//import akka.pattern.Patterns;
//import akka.testkit.TestActorRef;
//import scala.compat.java8.FutureConverters;
//import scala.concurrent.Await;
//import scala.concurrent.Future;
//import scala.concurrent.duration.Duration;
//
///**
// * @author Administrator
// *
// */
//public class AkkaDBTest {
//  ActorSystem system = ActorSystem.create();
//  TestActorRef<AkkaDB> actorRef = TestActorRef.create(system, Props.create(AkkaDB.class));
//
//  @Test
//  public void setTest() {
//    String key = "akaly", val = "33-26-34";
//    actorRef.tell(new SetRequest(key, val), noSender());
//    assertEquals(actorRef.underlyingActor().map.get(key), val);
//  }
//
//  @Test
//  public void getTest() throws Exception {
//    String key = "Ping", expect = "Pong";
//    actorRef.tell(new SetRequest(key, expect), noSender());
//    Future future = Patterns.ask(actorRef, new GetRequest(key), 1000);
//    assertEquals(expect, Await.result(future, Duration.create(1, TimeUnit.SECONDS)));
//  }
//
//  @Test
//  public void setNXTest() throws Exception {
//    String key = "Ping", expect = "Pong";
//    actorRef.tell(new SetNXRequest(key, expect), noSender());
//    Future future = Patterns.ask(actorRef, new SetNXRequest(key, expect), 1000);
//    assertEquals(false, Await.result(future, Duration.create(1, TimeUnit.SECONDS)));
//  }
//
//  @Test(expected = ExecutionException.class)
//  public void failureTest() throws Exception {
//    Future sFuture = Patterns.ask(actorRef, 88, 1000);
//    CompletableFuture jFuture = (CompletableFuture) FutureConverters.toJava(sFuture);
//    jFuture.get(1, TimeUnit.SECONDS);
//  }
//
//  @Test
//  public void callbackTest() {
//    get("key").thenAccept(System.out::println);
//  }
//
//  @Test
//  public void handleTest() {
//    get(true).handle((r, t) -> {
//      if (t != null)
//        System.out.println(t);
//      else
//        System.out.println("查询结果：" + r);
//      return r;
//    });
//  }
//
//  @Test
//  public void 组合多次查询() {
//    String key1 = "name", key2 = "mobile";
//    actorRef.tell(new SetRequest(key1, "万明丞"), noSender());
//    actorRef.tell(new SetRequest(key2, "18221201154"), noSender());
//    get("name").thenCombine(get("mobile"), (r1, r2) -> {
//      System.out.println(MessageFormat.format("{0}:{1}, {2}:{3}", key1, r1, key2, r2));
//      return r1.toString() + r2;
//    });
//  }
//
//  @Test
//  public void 批量操作() {
//    String key1 = "name", key2 = "mobile";
//    String val1 = "万明丞", val2 = "18221201154";
//    SetQueue queue = new SetQueue();
//    queue.add(new SetRequest(key1, val1));
//    queue.add(new SetRequest(key2, val2));
//    actorRef.tell(queue, noSender());
//    assertEquals(actorRef.underlyingActor().map.get(key1), val1);
//    assertEquals(actorRef.underlyingActor().map.get(key2), val2);
//  }
//
//  @Test
//  public void bufferTest() throws InterruptedException {
//    String key1 = "name", key2 = "mobile";
//    String val1 = "万明丞", val2 = "18221201154";
//    TestActorRef<TempActor> actor1 = TestActorRef.create(system, Props.create(TempActor.class));
//    TestActorRef<TempActor> actor2 = TestActorRef.create(system, Props.create(TempActor.class));
//    TestActorRef<TempActor> actor3 = TestActorRef.create(system, Props.create(TempActor.class));
//    TestActorRef<TempActor> actor4 = TestActorRef.create(system, Props.create(TempActor.class));
//    TestActorRef<TempActor> actor5 = TestActorRef.create(system, Props.create(TempActor.class));
//    RequestMap queue = new RequestMap();
//    queue.put(new SetRequest(key1, val1), actor1);
//    queue.put(new SetRequest(key2, val2), actor2);
//    queue.put(new GetRequest(key1), actor3);
//    queue.put(new GetRequest(key2), actor4);
//    queue.put(new SetNXRequest(key2, val1), actor5);
//    actorRef.tell(queue, noSender());
//    Thread.sleep(200);
//    assertEquals(true, actor1.underlyingActor().result);
//    assertEquals(true, actor2.underlyingActor().result);
//    assertEquals(val1, actor3.underlyingActor().result);
//    assertEquals(val2, actor4.underlyingActor().result);
//    assertEquals(false, actor5.underlyingActor().result);
//  }
//
//  public CompletionStage get(Object key) {
//    final CompletionStage cs = FutureConverters.toJava(Patterns.ask(actorRef, key, 1000));
//    return cs;
//  }
//
//  public static class TempActor extends AbstractActor {
//    public Object result;
//
//    @Override
//    public Receive createReceive() {
//      return receiveBuilder().matchAny(this::setResult).build();
//    }
//
//    public void setResult(Object o) {
//      System.out.println(o);
//      result = o;
//    }
//  }
//}
