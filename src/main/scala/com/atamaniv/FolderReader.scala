package com.atamaniv

import akka.actor.{Actor, ActorLogging, Props}
import com.atamaniv.Messages.GetCsvFiles

import scala.reflect.io.Path

object FolderReader {
  def props(): Props = Props(new FolderReader)
}

class FolderReader extends Actor with ActorLogging {

  override def preStart(): Unit = log.info(s"FolderReader $self has been started")
  override def postStop(): Unit = log.info(s"FolderReader $self has been stopped")

  override def receive: Receive = {
    case GetCsvFiles(folderPath: Path) => sender ! getFiles(folderPath)
  }

  private def getFiles(path: Path): List[Path] = {
    //TODO: under implementation
    Nil
  }
}
