/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-api
 * 文件名：	AbstractSetRequest.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月5日 - Administrator - 创建。
 */
package com.wmc.akkadb.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author wanmc
 *
 */
public class AbstractSetRequest extends AbstractRequest {
  private static final long serialVersionUID = 7445701106325077307L;

  private final String key;
  private final Object val;

  public AbstractSetRequest(String key, Object val) {
    this.key = key;
    this.val = val;
  }

  @Override
  public String getKey() {
    return key;
  }

  public Object getVal() {
    return val;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
}
