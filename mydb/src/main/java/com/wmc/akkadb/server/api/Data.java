/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	Response.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月6日 - wanmc - 创建。
 */
package com.wmc.akkadb.server.api;

import java.io.Serializable;

/**
 * @author wanmc
 *
 */
public class Data implements Serializable {
  private static final long serialVersionUID = -2569243293700211929L;

  private final long paxosId;
  private final Object val;

  public Data(long paxosId, Object val) {
    this.paxosId = paxosId;
    this.val = val;
  }

  public Long getPaxosId() {
    return paxosId;
  }

  public Object getVal() {
    return val;
  }
}
