package com.wmc.akkadb.client;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClientApplication {

  public static void main(String[] args) {
    Config config = ConfigFactory.defaultApplication();
    AkkaDBClient client = new AkkaDBClient("127.0.0.1:8088");
    boolean result = client.set("wmc", "c_nb");
    System.out.println(result);
    System.out.println(client.setNX("wmc", "c_nb"));
    System.out.println(client.get("c", String.class));
  }
}
