package com.atamaniv

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.atamaniv.Messages._

import scala.reflect.io.Path

object CrimeSupervisor {
  def props(): Props = Props(new CrimeSupervisor)
}

class CrimeSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("Crime supervisor started")
  override def postStop(): Unit = log.info("Crime supervisor stopped")

  private val fileReaders: Map[Path, ActorRef] = Map()

  override def receive: Receive = {
    case GetCsvFiles(path) => val folderReader = context.actorOf(FolderReader.props(), "ActorOfFolder_" + path)
      folderReader ! GetCsvFiles(path)

    case CsvFiles(files) => files.foreach(file => {
      fileReaders + (file -> context.actorOf(FileReader.props(), "ActorOfFile_" + file))
    })

    case PrintMessage(message) => println(message)
  }
}