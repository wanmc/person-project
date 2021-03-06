/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	mydb
 * 文件名：	AbstractRequest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月20日 - Administrator - 创建。
 */
package com.wmc.akka.mydb.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Administrator
 *
 */
public abstract class AbstractRequest {

  @Override
  public String toString() {
    return getClass().getSimpleName() + ":"
        + ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
