/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb
 * 文件名：	SetNXRequest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月22日 - Administrator - 创建。
 */
package com.wmc.akkadb.event;

/**
 * @author wanmc
 *
 */
public class SetNXRequest extends AbstractSetRequest {
  private static final long serialVersionUID = -4724800901522725892L;
  
  public SetNXRequest(String key, Object val) {
    super(key, val);
  }
}
