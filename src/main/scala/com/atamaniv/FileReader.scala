package com.atamaniv

import akka.actor.{Actor, ActorLogging, Props}
import com.atamaniv.Messages.ReadCsvFile
import com.atamaniv.model.Crime
import org.apache.spark.sql.{DataFrame, SparkSession}

object FileReader {
  def props(): Props = Props(new FileReader)
}

class FileReader extends Actor with ActorLogging {

  override def preStart(): Unit = log.info(s"FileReader $self has been started")
  override def postStop(): Unit = log.info(s"FileReader $self has been stopped")

  implicit val sparkSession = getSparkSession()

  override def receive: Receive = {
    case ReadCsvFile(filePath: String) =>
      println("asked to read " + filePath)
      sender ! readFile(filePath)
  }

  private def readFile(path: String): List[Crime] = {
    import sparkSession.implicits._
    val df = loadFileToDataFrame(path)
    val ids = df.map(row => row.getValuesMap(List("Crime ID")))
    println("Found " + ids.count() + " items")
    Nil
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
