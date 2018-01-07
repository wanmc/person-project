/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	Prepare.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月7日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.paxos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author wanmc
 *
 */
public class Prepare implements Serializable {
  private static final long serialVersionUID = 8714474581063719088L;

  private final PaxosKey paxosKey;
  private final int N;

  public Prepare(PaxosKey paxosKey, int N) {
    this.paxosKey = paxosKey;
    this.N = N;
  }

  public Prepare(long paxosId, String key, int N) {
    this.paxosKey = new PaxosKey(key, paxosId);
    this.N = N;
  }

  public PaxosKey getPaxosKey() {
    return paxosKey;
  }

  public Integer getN() {
    return N;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
