package com.atamaniv

import akka.actor.ActorSystem
import com.atamaniv.Messages.{PrintMessage, StartApplication}

import scala.reflect.io.{File, Path}

/***
  * Main Class of CrimeStatistics app, accept one argument, the path of folder,
  * It checks if folder exists and print back an error otherwise.
  * CrimeSupervisor actor is created here.
  * */

object Main extends App {

  final val SYSTEM_NAME = "crime-system"
  final val MAIN_ACTOR_NAME = "crime-supervisor"
  var isStarted: Boolean = false //For tests purposes only

  override def main(args: Array[String]): Unit = {

    args.toList match {
      case head :: Nil =>  if (isDirectoryExists(head)) startApplication(head)
      else println(s"please provide a correct path to folder, this one $head is not correct")
      case Nil => println("Please provide absolute path to your csv folder with crime data")
    }
  }

  private def startApplication(path: String): Unit = {
    val system = ActorSystem(SYSTEM_NAME)
    val supervisor = system.actorOf(CrimeSupervisor.props(), MAIN_ACTOR_NAME)
    supervisor ! PrintMessage("**************** Starting Application **********************")
    supervisor ! StartApplication(Path(path))
    isStarted = true
  }

  private def isDirectoryExists(directory: Path): Boolean = {
    File(directory).isDirectory
  }
}