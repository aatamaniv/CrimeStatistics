package com.atamaniv

import akka.actor.{Actor, ActorLogging, Props}
import com.atamaniv.Messages._

object CrimeSupervisor {
  def props(): Props = Props(new CrimeSupervisor)
}

class CrimeSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("Crime supervisor started")

  override def postStop(): Unit = log.info("Crime supervisor stopped")

  override def receive: Receive = {
    case StartApplication(path) =>
      val folderReader = context.actorOf(FolderReader.props(), "ActorOfFolder_" + path.name)
      folderReader ! GetCsvFiles(path)

    case CsvFiles(files) =>
      files.foreach(file => {
        val fileReader = context.actorOf(FileReader.props(), "ActorOfFile_" + file.name)
        fileReader ! ReadCsvFile(file.toString())
      })

    case CrimeDataUpdated(crimes) => //TODO Merge data and group be criteria

    case PrintMessage(message) => println(message)
  }
}