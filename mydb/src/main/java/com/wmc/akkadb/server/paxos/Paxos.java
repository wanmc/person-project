/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	PaxosInstance.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月7日 - wanmc - 创建。
 */
package com.wmc.akkadb.server.paxos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.wmc.akkadb.event.AbstractRequest;

/**
 * paxos实例<br>
 * 
 * 由request.key + id唯一标识一个paxos实例<br>
 * 
 * 一个paxos可以确定一个唯一的值
 * 
 * @author wanmc
 */
public class Paxos implements Serializable {
  private static final long serialVersionUID = -3799199932591395766L;

  private final long id;
  private final int promiseN;
  private final AbstractRequest request;

  public Paxos(long id, int promiseN, AbstractRequest request) {
    this.id = id;
    this.promiseN = promiseN;
    this.request = request;
  }

  /** 实例id，递增 */
  public Long getId() {
    return id;
  }

  /** 当前实例已承诺的编号，新的编号要大于本编号 */
  public int getPromiseN() {
    return promiseN;
  }

  public AbstractRequest getRequest() {
    return request;
  }
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
