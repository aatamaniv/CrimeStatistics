package com.atamaniv

import scala.reflect.io.Path

object Messages {
  case class GetCsvFiles(path: Path)
  case class CsvFiles(files: List[Path])
  case class ReadCsvFile(path: Path)
  case class Coordinates(longitude: Long, latitude: Long)
  case class Crime(coordinates: Coordinates)
}
