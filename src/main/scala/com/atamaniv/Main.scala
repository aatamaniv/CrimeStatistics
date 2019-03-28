package com.atamaniv

import akka.actor.ActorSystem
import com.atamaniv.Messages.{CreateFolderReaderActor, PrintMessage}

object Main extends Greeting with App {
  println(startMessage)

  override def main(args: Array[String]): Unit = {
    val system = ActorSystem("crime-system")

    try {
      val supervisor = system.actorOf(CrimeSupervisor.props(), "crime-supervisor")
      supervisor ! PrintMessage("Test Message")
      supervisor ! CreateFolderReaderActor("resources.crimes")
    } finally {
      println("exit")
    }
  }
}

trait Greeting {
  lazy val startMessage: String = "Starting Crime Reports application"
}
