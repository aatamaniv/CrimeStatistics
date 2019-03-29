package com.atamaniv

import akka.actor.ActorSystem
import com.atamaniv.Messages.{PrintMessage, StartApplication}

import scala.reflect.io.Path

object Main extends Greeting with App {
  println(startMessage)

  override def main(args: Array[String]): Unit = {
    val system = ActorSystem("crime-system")

      val supervisor = system.actorOf(CrimeSupervisor.props(), "crime-supervisor")
      supervisor ! PrintMessage("**************** Starting Application **********************")
      supervisor ! StartApplication(Path("/Users/andriy/IdeaProjects/CrimeStatistics/src/main/resources/crimes"))
  }
}

trait Greeting {
  lazy val startMessage: String = "Starting Crime Reports application"
}
