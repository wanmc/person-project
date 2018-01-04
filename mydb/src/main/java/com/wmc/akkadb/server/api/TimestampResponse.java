/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	TimeStampRequest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月30日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.api;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import akka.actor.ActorRef;

/**
 * @author wanmc
 *
 */
public class TimestampResponse implements Serializable {
  private static final long serialVersionUID = 6065324330201006555L;

  private final ActorRef node;
  private final Object response;
  private final long timestamp;

  public TimestampResponse(ActorRef node, Object response, long timestamp) {
    this.node = node;
    this.response = response;
    this.timestamp = timestamp;
  }

  public Object getResponse() {
    return response;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public ActorRef getNode() {
    return node;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, "node");
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(obj, this, "node");
  }
}
