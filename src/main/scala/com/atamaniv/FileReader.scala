package com.atamaniv

import akka.actor.{Actor, Props}
import com.atamaniv.Messages.GetCsvFiles

import scala.reflect.io.Path

object FileReader {
  def props(): Props = Props(new FileReader)
}

class FileReader extends Actor {

  override def receive: Receive = {
    case GetCsvFiles(folderPath: Path) => sender ! getFiles(folderPath)
  }

  private def getFiles(path: Path): List[Path] = {
    //TODO: under implementation
    Nil
  }
}
