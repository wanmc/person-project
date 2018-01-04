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

import com.wmc.akkadb.event.AbstractRequest;

/**
 * @author wanmc
 *
 */
public class TimestampRequest implements Serializable {
  private static final long serialVersionUID = 6065324330201006555L;

  private final AbstractRequest request;
  private final long timestamp;

  public TimestampRequest(AbstractRequest request, long timestamp) {
    this.request = request;
    this.timestamp = timestamp;
  }

  public AbstractRequest getRequest() {
    return request;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
