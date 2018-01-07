package com.wmc.akkadb.server;

import com.typesafe.config.Config;
import com.wmc.akkadb.server.cluster.ClusterDbActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;

public class ClusterApplication {

  public static void main(String[] args) {
    Config config = ConfigLoader.get("db-server.conf");
    ActorSystem system = ActorSystem.create("Akka-db-system-server-cluster", config);
    ActorRef worker = system.actorOf(Props.create(ClusterDbActor.class),
        config.getString("akka.actor.dispatcher_name"));
    ClusterClientReceptionist.get(system).registerService(worker);
  }
}
