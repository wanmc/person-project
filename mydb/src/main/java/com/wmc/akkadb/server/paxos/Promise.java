/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	Promise.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月7日 - wanmc - 创建。
 */
package com.wmc.akkadb.server.paxos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.wmc.akkadb.event.AbstractRequest;

/**
 * 承诺<br>
 * 
 * 不再响应编号小于n的prepare
 * 
 * @author wanmc
 */
public class Promise implements Serializable {
  private static final long serialVersionUID = -1879982272028268224L;

  private final int acceptedN;
  private final AbstractRequest acceptedV;

  public Promise(int acceptedN, AbstractRequest acceptedV) {
    this.acceptedN = acceptedN;
    this.acceptedV = acceptedV;
  }

  public Integer getAcceptedN() {
    return acceptedN;
  }

  public AbstractRequest getAcceptedV() {
    return acceptedV;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
