/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	AkkamyDBTest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月20日 - Administrator - 创建。
 */
package com.wmc.akka.mydb.test;

import org.junit.Assert;
import org.junit.Test;

import com.wmc.akka.mydb.AkkaDB;
import com.wmc.akka.mydb.messages.SetRequest;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;

/**
 * @author Administrator
 *
 */
public class AkkaDBTest {
  ActorSystem system = ActorSystem.create();
  
  @Test
  public void setTest() {
    String key = "akaly", val = "33-26-34";
    TestActorRef<AkkaDB> actorRef = TestActorRef.create(system, Props.create(AkkaDB.class));
    actorRef.tell(new SetRequest(key, val), TestActorRef.noSender());
    Assert.assertEquals(actorRef.underlyingActor().map.get(key), val);
  }
}
