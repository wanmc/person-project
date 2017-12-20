/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	SetRequest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月20日 - Administrator - 创建。
 */
package com.wmc.akka.mydb.messages;

/**
 * @author Administrator
 *
 */
public final class SetRequest extends AbstractRequest {
  private final String key;
  private final Object val;

  public SetRequest(String key, Object val) {
    this.key = key;
    this.val = val;
  }

  public String getKey() {
    return key;
  }

  public Object getVal() {
    return val;
  }
}
