/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	KeyNotFoundException.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月22日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.commons;

/**
 * @author Administrator
 *
 */
public class KeyNotFoundException extends Exception {
  private static final long serialVersionUID = -6753828966576468028L;
  
  private final String key;
  
  public KeyNotFoundException(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
