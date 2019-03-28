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
    case CreateFileReaderActor(path) => sender ! context.actorOf(FileReader.props(), "ActorOfFile_" + path)
    case CreateFolderReaderActor(path) => sender ! context.actorOf(FolderReader.props(), "ActorOfFolder_" + path)
    case FolderReaderCreated(ref) => ref ! GetCsvFiles("resources.crimes")
    case CsvFiles(files) => files.foreach(self ! CreateFileReaderActor(_))
    case PrintMessage(message) => println(message)
  }
}