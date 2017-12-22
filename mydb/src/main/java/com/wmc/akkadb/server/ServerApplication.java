package com.wmc.akkadb.server;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ServerApplication {

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create("Akka-db-system-server",
        ConfigFactory.load("db-server.conf"));
    ActorRef actor = system.actorOf(Props.create(AkkaDB.class), "db-actor");
    System.out.println(actor.path());
  }
}
