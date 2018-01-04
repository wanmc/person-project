/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-api
 * 文件名：	FlushEvent.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月25日 - Administrator - 创建。
 */
package com.wmc.akkadb.event;

import java.io.Serializable;

/**
 * 将buffer中的事件发送到db
 */
public class FlushEvent implements Serializable {
  private static final long serialVersionUID = -3560998470320722702L;
}