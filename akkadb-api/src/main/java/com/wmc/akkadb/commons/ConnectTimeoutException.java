/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-api
 * 文件名：	ConnectTimeoutException.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月24日 - wanmc - 创建。
 */
package com.wmc.akkadb.commons;

import java.text.MessageFormat;

/**
 * 数据库连接超时异常
 * 
 * @author wanmc
 */
public class ConnectTimeoutException extends Exception {
  private static final long serialVersionUID = 2674458752221104410L;

  public ConnectTimeoutException() {
  }

  public ConnectTimeoutException(String pattern, Object... arguments) {
    super(MessageFormat.format(pattern, arguments));
  }

  public ConnectTimeoutException(Throwable t) {
    super(t);
  }

  public ConnectTimeoutException(Throwable t, String pattern, Object... arguments) {
    super(MessageFormat.format(pattern, arguments), t);
  }
}
