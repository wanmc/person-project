/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-server
 * 文件名：	Config.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月30日 - Administrator - 创建。
 */
package com.wmc.akkadb.server;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author Administrator
 *
 */
public class ConfigLoader {
  private static Config cfg;

  public static Config get() {
    return cfg;
  }

  public static Config get(String name) {
    cfg = ConfigFactory.load(name);
    return cfg;
  }
}
