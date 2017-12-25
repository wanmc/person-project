/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	akkadb-client
 * 文件名：	ClientTest.java
 * 模块说明：	
 * 修改历史：
 * 2017年12月22日 - wanmc - 创建。
 */
package com.wmc.akkadb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;

import com.typesafe.config.ConfigFactory;
import com.wmc.akkadb.client.BufferAkkaDBClient;

import akka.actor.ActorSystem;

/**
 * @author wanmc
 *
 */
public class BufferClientTest {
  private static final ActorSystem system = ActorSystem.create("Akka-db-system-client");
  BufferAkkaDBClient client = new BufferAkkaDBClient(system,
      ConfigFactory.defaultApplication().getString("akka.remote_url"), 50000);

  @Test
  public void set() throws Exception {
    String key = "akaly"; URL val = new URL("http://www.baidu.com");
    boolean set = client.set(key, val);
    assertTrue(set);
    assertEquals(val, client.get(key, val.getClass()));
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

  @Test
  public void asyncGet() throws Exception {
    String key = "akaly", val = "33-26-34";
    client.asyncSet(key, val);
    String actual = client.asyncGet(key, String.class).get();
    assertEquals(val, actual);
  }
}
