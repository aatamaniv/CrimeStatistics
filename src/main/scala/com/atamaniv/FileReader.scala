package com.atamaniv

import akka.actor.{Actor, ActorLogging, Props}
import com.atamaniv.Messages.{Crime, ReadCsvFile}

import scala.reflect.io.Path

object FileReader {
  def props(): Props = Props(new FileReader)
}

class FileReader extends Actor with ActorLogging {

  override def preStart(): Unit = log.info(s"FileReader $self has been started")
  override def postStop(): Unit = log.info(s"FileReader $self has been stopped")

  override def receive: Receive = {
    case ReadCsvFile(filePath: Path) => sender ! readFile(filePath)
  }

  private def readFile(path: Path): List[Crime] = {
    //TODO: under implementation
    Nil
  }
}
