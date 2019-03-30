package com.atamaniv

import akka.actor.{Actor, ActorLogging, Props}
import com.atamaniv.Messages.{RawData, ReadCsvFile}
import com.atamaniv.model.Crime
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object FileReader {
  def props(): Props = Props(new FileReader)
}

object ColumnNames extends Enumeration {
  type ColumnNames = Value

  val ID = Value("Crime ID")
  val MONTH = Value("Month")
  val REPORTED_BY = Value("Reported by")
  val FALLS_WITHIN = Value("Falls within")
  val LONGITUDE = Value("Longitude")
  val LATITUDE = Value("Latitude")
  val LOCATION = Value("Location")
  val LSOA_CODE = Value("LSOA code")
  val LSOA_NAME = Value("LSOA name")
  val CRIME_TYPE = Value("Crime type")
  val LAST_OUTCOME = Value("Last outcome category")
  val CONTEXT = Value("Context")
}

class FileReader extends Actor with ActorLogging {

  import scala.collection.JavaConverters._

  override def preStart(): Unit = log.info(s"FileReader $self has been started")

  override def postStop(): Unit = log.info(s"FileReader $self has been stopped")

  implicit val sparkSession = getSparkSession()

  override def receive: Receive = {
    case ReadCsvFile(filePath: String) =>
      println("asked to read " + filePath)
      sender ! RawData(readFile(filePath))
  }

  private def readFile(path: String): Dataset[Row] = {
    //import sparkSession.implicits._
    //crimesWithId.map(mapToCrime).collectAsList().asScala.toList
    val df = loadFileToDataFrame(path)
    df.filter(_.get(0) != null).cache()
  }

  private def getSparkSession(): SparkSession = {
    org.apache.spark.sql.SparkSession.builder
      .master("local")
      .appName("Spark CSV Reader")
      .getOrCreate
  }

  private def loadFileToDataFrame(path: String)(implicit ss: SparkSession): DataFrame = {
    val df = ss.read
      .format("csv")
      .option("header", "true") //first line in file has headers
      .option("mode", "DROPMALFORMED")
      .load(path)
    df
  }
}
