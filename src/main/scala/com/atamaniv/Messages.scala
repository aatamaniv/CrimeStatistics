package com.atamaniv

import akka.actor.ActorRef

import scala.reflect.io.Path

object Messages {
  case class CreateFileReaderActor(path: Path)
  case class CreateFolderReaderActor(path: Path)
  case class FolderReaderCreated(ref: ActorRef)
  case class FileReaderCreated(ref: ActorRef)
  case class GetCsvFiles(path: Path)
  case class CsvFiles(files: List[Path])
  case class ReadCsvFile(path: Path)
  case class Coordinates(longitude: Long, latitude: Long)
  case class Crime(coordinates: Coordinates)
  case class PrintMessage(message: String)
}
