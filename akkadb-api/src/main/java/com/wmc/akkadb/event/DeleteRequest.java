/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-api
 * 文件名：	DeleteRequest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月22日 - Administrator - 创建。
 */
package com.wmc.akkadb.event;

/**
 * @author Administrator
 *
 */
public class DeleteRequest extends AbstractRequest {
  private static final long serialVersionUID = -6449217572108808175L;

  private final String key;

  public DeleteRequest(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

}
