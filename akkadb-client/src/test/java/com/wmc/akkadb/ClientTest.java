/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-client
 * 文件名：	ClientTest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月22日 - Administrator - 创建。
 */
package com.wmc.akkadb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import org.junit.Test;

import com.typesafe.config.ConfigFactory;
import com.wmc.akkadb.client.AkkaDBClient;

/**
 * @author Administrator
 *
 */
public class ClientTest {
  AkkaDBClient client = new AkkaDBClient(
      ConfigFactory.defaultApplication().getString("akka_db.remote_url"));

  @Test
  public void set() {
    String key = "akaly", val = "33-26-34";
    boolean set = client.set(key, val);
    assertTrue(set);
    String actual = client.get(key, String.class);
    assertEquals(val, actual);
  }

  @Test
  public void setNX() {
    String key = "akaly", val = "33-26-34";
    if (client.get(key, String.class) == null) {
      boolean set = client.setNX(key, val);
      assertTrue(set);
      assertTrue(!client.setNX(key, val));
    } else {
      boolean set = client.setNX(key, val);
      assertTrue(!set);
    }
  }

  @Test
  public void delete() {
    String key = "akaly", val = "33-26-34";
    boolean set = client.set(key, val);
    assertTrue(set);
    String actual = client.get(key, String.class);
    assertEquals(val, actual);
    client.delete(key);
    actual = client.get(key, String.class);
    assertNull(actual);
  }
}
