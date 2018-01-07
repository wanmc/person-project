/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	Proposer.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月7日 - wanmc - 创建。
 */
package com.wmc.akkadb.server.paxos;

/**
 * 提议者
 * 
 * @author wanmc
 */
public interface Proposer {

  /** prepare阶段的请求 */
  void prepareRequest();

  /** accept阶段的请求 */
  void acceptRequest();
}
