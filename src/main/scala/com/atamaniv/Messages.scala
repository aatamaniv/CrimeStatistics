package com.atamaniv

import com.atamaniv.model.Crime
import org.apache.spark.sql.{Dataset, Row}

import scala.reflect.io.Path

object Messages {
  case class GetCsvFiles(path: Path)
  case class CsvFiles(files: List[Path])
  case class ReadCsvFile(path: String)
  case class PrintMessage(message: String)
  case class StartApplication(folderPath: Path)
  case class CrimeData(crimes: List[Crime])
  case class RawData(dataset: Dataset[Row])
  case object SortDataAndPrintTop5
}
