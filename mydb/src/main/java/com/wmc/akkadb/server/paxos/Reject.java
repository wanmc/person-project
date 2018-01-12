/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	Reject.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月7日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.paxos;

import java.io.Serializable;

/**
 * 拒绝（一般用于提议冲突）
 * 
 * @author wanmc
 */
public class Reject implements Serializable {
  private static final long serialVersionUID = -4104521882663927937L;

  private long paxosId;

  public Long getPaxosId() {
    return paxosId;
  }

  public void setPaxosId(long paxosId) {
    this.paxosId = paxosId;
  }
}
