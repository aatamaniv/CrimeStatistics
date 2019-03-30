package com.atamaniv

import com.atamaniv.model.Crime

import scala.reflect.io.Path

object Messages {
  case class GetCsvFiles(path: Path)
  case class CsvFiles(files: List[Path])
  case class ReadCsvFile(path: String)
  case class PrintMessage(message: String)
  case class StartApplication(folderPath: Path)
  case class CrimeDataUpdated(crimes: List[Crime])
}
