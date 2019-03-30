package com.atamaniv

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.atamaniv.ColumnNames.ColumnNames
import com.atamaniv.Messages._
import com.atamaniv.model.{Coordinates, Crime}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, Row, SparkSession}

import scala.util.Try

/***
  * CrimeSupervisor class is responsible for lifecycle of FolderReader actor and FileReader actors,
  * It consumes datasets form fileReaders and prints merged result based of Criteria
  * */

object CrimeSupervisor {
  def props(): Props = Props(new CrimeSupervisor)
}

object ColumnNameConversions {
  implicit def ColumnNamesToString(value: ColumnNames) =
    value.toString
}

class CrimeSupervisor extends Actor with ActorLogging {
  var mainDataSet: Option[Dataset[Row]] = None
  var expectedFiles = 0

  override def preStart(): Unit = log.info("Crime supervisor started")

  override def postStop(): Unit = log.info("**************** Application has been terminated **********************")

  override def receive: Receive = {
    case StartApplication(path) =>
      val folderReader = context.actorOf(FolderReader.props(), "ActorOfFolder_" + path.name)
      folderReader ! GetCsvFiles(path)

    case CsvFiles(files) =>
      expectedFiles = files.length
      files.foreach(file => {
        val fileReader = context.actorOf(FileReader.props(), "ActorOfFile_" + file.name)
        fileReader ! ReadCsvFile(file.toString())
      })

    case RawData(dataset) =>
      expectedFiles -= 1
      log.info("Merging " + dataset.count() + " rows")
      mainDataSet = mergeDatasets(dataset)
      mainDataSet.foreach(ds => log.info(s"Total number of rows: ${ds.count()}"))
      if (expectedFiles == 0) {
        self ! SortDataAndPrintTop5
      }

    case SortDataAndPrintTop5 =>
      mainDataSet.foreach(crimeList)
      self ! PoisonPill

    case PrintMessage(message) => println(message)
  }

  private def mergeDatasets(value: Dataset[Row]): Option[Dataset[Row]] = {
    mainDataSet match {
      case Some(dataset) => Some(dataset.union(value))
      case None => Some(value)
    }
  }

  private def crimeList(dataSet: Dataset[Row]): Unit = {
    val session = getSparkSession()
    import ColumnNameConversions._
    import session.implicits._
    val top5Result = dataSet.map(row => {

      def convertToString(income: Any): Option[String] = {
        if (income != null) {
          Some(income.asInstanceOf[String])
        } else {
          None
        }
      }

      val id = row.getAs(ColumnNames.ID).asInstanceOf[String]
      val longitude = row.getAs(ColumnNames.LONGITUDE).asInstanceOf[String]
      val latitude = row.getAs(ColumnNames.LATITUDE).asInstanceOf[String]
      val reportedBy = convertToString(row.getAs(ColumnNames.REPORTED_BY))
      val fallsWithin = convertToString(row.getAs(ColumnNames.FALLS_WITHIN))
      val location = convertToString(row.getAs(ColumnNames.LOCATION))
      val lsoaCode = convertToString(row.getAs(ColumnNames.LSOA_CODE))
      val lsoaName = convertToString(row.getAs(ColumnNames.LSOA_NAME))
      val crimeType = convertToString(row.getAs(ColumnNames.CRIME_TYPE))
      val lastOutcome = convertToString(row.getAs(ColumnNames.LAST_OUTCOME))
      val context = convertToString(row.getAs(ColumnNames.CONTEXT))

      Crime(crimeId = id,
        coordinates = Coordinates(Try(longitude.toDouble).toOption.getOrElse(0L), Try(latitude.toDouble).toOption.getOrElse(0L)),
        reportedBy = reportedBy,
        fallsWithin = fallsWithin,
        location = location,
        lsoaCode = lsoaCode,
        lsoaName = lsoaName,
        crimeType = crimeType,
        lastOutcome = lastOutcome,
        context = context
      )

    }).groupBy("coordinates")
      .agg(count("crimeId") as "Count", collect_set("crimeType") as "Crime Type")
      .sort($"Count".desc)
      .limit(5)

    top5Result.show(false)
  }

  private def getSparkSession(): SparkSession = {
    org.apache.spark.sql.SparkSession.builder
      .master("local")
      .appName("CrimeProcessor")
      .getOrCreate
  }
}