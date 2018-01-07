/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	Accept.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月7日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.paxos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.wmc.akkadb.event.AbstractRequest;

/**
 * 提案
 * 
 * @author wanmc
 */
public class Proposal implements Serializable {
  private static final long serialVersionUID = 3499839391750122015L;

  private final long paxosId;
  private final Integer n;
  private final AbstractRequest val;

  public Proposal(long paxosId, Integer n, AbstractRequest val) {
    this.paxosId = paxosId;
    this.n = n;
    this.val = val;
  }

  /** paxos实例的id */
  public Long getPaxosId() {
    return paxosId;
  }

  public PaxosKey getPaxosKey() {
    return new PaxosKey(val.getKey(), paxosId);
  }

  /** 提案编号 */
  public Integer getN() {
    return n;
  }

  /** 提案值 */
  public AbstractRequest getVal() {
    return val;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
