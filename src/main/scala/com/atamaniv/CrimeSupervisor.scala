package com.atamaniv

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.atamaniv.Messages._
import com.atamaniv.model.ColumnNames.ColumnNames
import com.atamaniv.model.{ColumnNames, Coordinates, Crime}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

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
  override def preStart(): Unit = log.info("Crime supervisor started")

  override def postStop(): Unit = log.info("**************** Application has been terminated **********************")

  implicit val sparkSession = getSparkSession()

  override def receive: Receive = {
    case CalculateAndPrintTop5(folderPath) =>
      val dataset = loadFolderData(folderPath)
      printTop5(dataset)
      self ! PoisonPill

    case PrintMessage(message) => println(message)
  }

  private def printTop5(dataSet: Dataset[Row]): Unit = {
    val session = getSparkSession()
    import ColumnNameConversions._
    import session.implicits._
    val top5Result = dataSet.map(row => {

      val id = row.getAs[String](ColumnNames.ID)
      val longitude = row.getAs[String](ColumnNames.LONGITUDE)
      val latitude = row.getAs[String](ColumnNames.LATITUDE)
      val reportedBy = row.getAs[String](ColumnNames.REPORTED_BY)
      val fallsWithin = row.getAs[String](ColumnNames.FALLS_WITHIN)
      val location = row.getAs[String](ColumnNames.LOCATION)
      val lsoaCode = row.getAs[String](ColumnNames.LSOA_CODE)
      val lsoaName = row.getAs[String](ColumnNames.LSOA_NAME)
      val crimeType = row.getAs[String](ColumnNames.CRIME_TYPE)
      val lastOutcome = row.getAs[String](ColumnNames.LAST_OUTCOME)
      val context = row.getAs[String](ColumnNames.CONTEXT)

      Crime(crimeId = id,
        coordinates = Coordinates(Try(longitude.toDouble).toOption.getOrElse(0L), Try(latitude.toDouble).toOption.getOrElse(0L)),
        reportedBy = Some(reportedBy),
        fallsWithin = Some(fallsWithin),
        location = Some(location),
        lsoaCode = Some(lsoaCode),
        lsoaName = Some(lsoaName),
        crimeType = Some(crimeType),
        lastOutcome = Some(lastOutcome),
        context = Some(context)
      )

    }).groupBy("coordinates")
      .agg(count("crimeId") as "Count", collect_set("crimeType") as "Crime Type")
      .sort($"Count".desc)
      .limit(5)

    top5Result.show(false)
  }

  private def loadFolderData(path: String): Dataset[Row] = {
    val df = loadDataToDataFrame(path)
    df.filter(_.get(0) != null).cache()
  }

  private def getSparkSession(): SparkSession = {
    org.apache.spark.sql.SparkSession.builder
      .master("local")
      .appName("Spark CSV Reader")
      .getOrCreate
  }

  private def loadDataToDataFrame(path: String)(implicit ss: SparkSession): DataFrame = {
    ss.read.option("header", "true").csv(path + "/*.csv")
  }
}