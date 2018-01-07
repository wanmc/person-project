/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	PaxosKey.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月7日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.paxos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.alibaba.fastjson.JSON;

/**
 * @author wanmc
 *
 */
public class PaxosKey implements Serializable {
  private static final long serialVersionUID = -7727913407736506149L;

  private long paxosId;
  private String key;

  public PaxosKey(String key, long paxosId) {
    this.key = key;
    this.setPaxosId(paxosId);
  }

  public long getPaxosId() {
    return paxosId;
  }

  public void setPaxosId(long paxosId) {
    this.paxosId = paxosId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
