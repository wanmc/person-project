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
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.wmc.akkadb.client.AkkaDBClient;

import akka.actor.ActorSystem;

/**
 * @author wanmc
 *
 */
public class ClientTest {
  private static final Config cfg = ConfigFactory.load("sample.conf");
  private static final ActorSystem system = ActorSystem.create("Akka-db-system-client", cfg);
  AkkaDBClient client = new AkkaDBClient(system, 500000);

  @Test
  public void set() throws Exception {
    String key = "akaly";
    URL val = new URL("http://www.baidu.com");
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
    client.set(key, val);
    String actual = client.asyncGet(key, String.class).get();
    assertEquals(val, actual);
  }

  @Test
  public void get() throws Exception {
    String key = "akaly", val = "33-26-34";
    String actual = client.asyncGet(key, String.class).get();
    assertEquals(val, actual);
  }

  @Test
  public void consurrentSet() throws Exception {
    final AtomicInteger errors = new AtomicInteger(0);
    Thread t1 = new Thread(runnable(errors));
    Thread t2 = new Thread(runnable(errors));
    Thread t3 = new Thread(runnable(errors));
    Thread t4 = new Thread(runnable(errors));
    Thread t5 = new Thread(runnable(errors));
    long b = System.currentTimeMillis();
    t1.start();
    t2.start();
    t3.start();
    t4.start();
    t5.start();
    t1.join();
    t2.join();
    t3.join();
    t4.join();
    t5.join();
    System.out.println(System.currentTimeMillis() - b + ": " + errors.get());
  }

  public Runnable runnable(AtomicInteger errors) {
    final String key = "akaly" + new Random().nextInt(30);
    final Random random = new Random();
    return new Runnable() {
      @Override
      public void run() {
        AkkaDBClient client = new AkkaDBClient(system, 2000);
        for (int i = 0; i < 500; i++) {
          try {
            client.set(key, random.nextInt(2000));
          } catch (Exception e) {
            System.out.println(e);
            errors.getAndIncrement();
          }
        }
      }
    };
  }
}
