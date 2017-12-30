package com.wmc.akkadb.server;

import com.typesafe.config.ConfigFactory;
import com.wmc.akkadb.server.cluster.ClusterController;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;

public class ClusterApplication {

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create("Akka-db-system-server-cluster",
        ConfigFactory.load("db-server.conf"));
    system.actorOf(Props.create(ClusterController.class), "clusterController");
    ActorRef woker = system.actorOf(Props.create(AkkaDB.class), "db-server");
    ClusterClientReceptionist.get(system).registerService(woker);
  }
}
