/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	Acceptor.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月7日 - Administrator - 创建。
 */
package com.wmc.akkadb.server.paxos;

/**
 * 决议者
 * 
 * @author wanmc
 */
public interface Acceptor {

  /** 获得已承诺的编号N */
  int getPromised(PaxosKey key);

  /** 承诺编号N */
  void promise(PaxosKey key, int n);

  /** 已接受的编号最大的提案 */
  Proposal getAccepted(PaxosKey key);

  /** 接受提案 */
  void accept(PaxosKey key, Proposal proposal);

  /** prepare阶段的响应  */
  void prepareResponse(Prepare prepare);

  /** accept阶段的响应 */
  void acceptResponse(Proposal proposal);
}
