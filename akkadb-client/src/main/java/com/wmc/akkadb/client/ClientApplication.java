package com.wmc.akkadb.client;

public class ClientApplication {

	public static void main(String[] args) {
	  AkkaDBClient client = new AkkaDBClient("127.0.0.1:8088");
	  boolean result = client.set("wmc", "c_nb");
	  System.out.println(result);
	}
}
