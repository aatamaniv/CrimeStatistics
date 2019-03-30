package com.atamaniv

import akka.actor.{Actor, ActorLogging, Props}
import com.atamaniv.Messages._
import org.apache.spark.sql.{Dataset, Row}

object CrimeSupervisor {
  def props(): Props = Props(new CrimeSupervisor)
}

class CrimeSupervisor extends Actor with ActorLogging {
  var mainDataSet: Option[Dataset[Row]] = None

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

    case RawData(dataset) =>
      log.info("Merging " + dataset.count() + " rows")
      mainDataSet = mergeDatasets(dataset)
      mainDataSet.foreach(ds => log.info(s"Total number of rows: ${ds.count()}"))

    case PrintMessage(message) => println(message)
  }

  private def mergeDatasets(value: Dataset[Row]): Option[Dataset[Row]] = {
      mainDataSet match {
        case Some(dataset) => Some(dataset.union(value))
        case None => Some(value)
      }
  }
}