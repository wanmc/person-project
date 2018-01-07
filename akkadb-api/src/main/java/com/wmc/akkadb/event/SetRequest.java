/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	SetRequest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月20日 - Administrator - 创建。
 */
package com.wmc.akkadb.event;

/**
 * @author wanmc
 *
 */
public final class SetRequest extends AbstractSetRequest {
  private static final long serialVersionUID = 5427501909268521015L;

  public SetRequest(String key, Object val) {
    super(key, val);
  }
}
