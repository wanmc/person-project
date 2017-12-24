/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-api
 * 文件名：	StringRequest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月24日 - Administrator - 创建。
 */
package com.wmc.akkadb.event;

/**
 * @author Administrator
 *
 */
public class StringRequest {
  public static final String CONNECT = StringRequest.class.getName() + "::connect";
  public static final String CONNECTED = StringRequest.class.getName() + "::connected";
  public static final String CONNECT_CHECK = StringRequest.class.getName() + "connect";
}
