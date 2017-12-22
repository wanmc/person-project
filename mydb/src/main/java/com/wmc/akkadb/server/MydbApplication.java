package com.wmc.akkadb.server;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

//@SpringBootApplication
public class MydbApplication {

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create("Akka-db-system",
        ConfigFactory.load("application.conf"));
    system.actorOf(Props.create(AkkaDB.class), "db-actor");
    // SpringApplication.run(MydbApplication.class, args);
  }
}
