/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	SetRequest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月20日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.event;

/**
 * @author Administrator
 *
 */
public final class GetRequest extends AbstractRequest {
  private static final long serialVersionUID = -944428817395482358L;

  private final String key;

  public GetRequest(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
