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
public class RequestMap extends LinkedHashMap<AbstractRequest, ActorRef> {
  private static final long serialVersionUID = -4457200652548411912L;
}
