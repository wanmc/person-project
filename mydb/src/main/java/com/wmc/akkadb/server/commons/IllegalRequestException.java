/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	IllegalRequestException.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月21日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.commons;

import java.text.MessageFormat;

/**
 * @author Administrator
 *
 */
public class IllegalRequestException extends Exception {
  private static final long serialVersionUID = -8635004192733132511L;

  public IllegalRequestException() {
    // Do nothing
  }

  public IllegalRequestException(String pattern, Object... arguments) {
    super(MessageFormat.format(pattern, arguments));
  }

  public IllegalRequestException(Throwable t) {
    super(t);
  }

  public IllegalRequestException(Throwable t, String pattern, Object... arguments) {
    super(MessageFormat.format(pattern, arguments), t);
  }
}
