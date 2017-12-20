/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	AkkaDB.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月20日 - Administrator - 创建。
 */
package com.wmc.akka.mydb;

import java.util.HashMap;
import java.util.Map;

import com.wmc.akka.mydb.messages.SetRequest;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author Administrator
 *
 */
public class AkkaDB extends AbstractActor {
  protected final LoggingAdapter log = Logging.getLogger(context().system(), this);

  public final Map<String, Object> map = new HashMap<>();

  @Override
  public Receive createReceive() {
    return receiveBuilder().match(SetRequest.class, e -> {
      log.info("receive set request: {}", e);
      map.put(e.getKey(), e.getVal());
    }).matchAny(o -> log.info("未知的消息：{}", o)).build();
  }

}
