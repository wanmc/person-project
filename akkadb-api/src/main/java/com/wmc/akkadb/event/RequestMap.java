/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-api
 * 文件名：	RequestQueue.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月24日 - Administrator - 创建。
 */
package com.wmc.akkadb.event;

import java.util.LinkedHashMap;

import akka.actor.ActorRef;

/**
 * @author wanmc
 */
public class RequestMap<R extends AbstractRequest> extends LinkedHashMap<R, ActorRef> {
  private static final long serialVersionUID = -4457200652548411912L;
  
  public static void main(String[] args) {
    RequestMap<AbstractRequest> map1 = new RequestMap();
    System.out.println(map1.getClass().equals(RequestMap.class));
  }
}
