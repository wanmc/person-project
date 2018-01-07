/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	InnerRequest.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月6日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.api;

import java.io.Serializable;

import com.wmc.akkadb.event.AbstractRequest;

/**
 * 广播请求，用于副本节点内部传播
 * 
 * @author wanmc
 */
public class PublishRequest implements Serializable {
  private static final long serialVersionUID = 6889675010364397338L;

  private final AbstractRequest request;

  public PublishRequest(AbstractRequest request) {
    this.request = request;
  }

  public AbstractRequest getRequest() {
    return request;
  }
}
