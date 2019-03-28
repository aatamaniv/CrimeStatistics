package com.atamaniv

import akka.actor.ActorSystem

object Main extends Greeting with App {
  println(startMessage)

  override def main(args: Array[String]): Unit = {
    val system = ActorSystem("crime-system")

    try {
      val supervisor = system.actorOf(IotSupervisor.props(), "crime-supervisor")
    } finally {
      println("exit")
    }
  }
}

trait Greeting {
  lazy val startMessage: String = "Starting Crime Reports application"
}
