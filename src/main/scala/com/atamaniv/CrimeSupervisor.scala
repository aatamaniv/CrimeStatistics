package com.atamaniv

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.atamaniv.ColumnNames.ColumnNames
import com.atamaniv.Messages._
import com.atamaniv.model.{Coordinates, Crime}
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.functions._

import scala.collection.JavaConverters._
import scala.util.Try

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

  override def postStop(): Unit = log.info("Crime supervisor stopped")

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
        self ! SortData
      }

    case SortData =>
      val crimes = mainDataSet.map(ds => crimeList(ds)).map(_.toString).mkString
      self ! PrintMessage(crimes)
      self ! PoisonPill

    case PrintMessage(message) => println(message)
  }

  private def mergeDatasets(value: Dataset[Row]): Option[Dataset[Row]] = {
    mainDataSet match {
      case Some(dataset) => Some(dataset.union(value))
      case None => Some(value)
    }
  }

  private def mapToCrime(row: Row): Crime = {
    import ColumnNameConversions._

    val id = row.getAs(ColumnNames.ID).asInstanceOf[String]

    println("Converted ID " + id)
    Crime(id, Coordinates(0, 0))
  }

  private def crimeList(dataSet: Dataset[Row]): List[Crime] = {
    val session = getSparkSession()
    import session.implicits._
    val countOfGrouped = dataSet.map(row => {

      def convertToString(income: Any): Option[String] = {
        if (income != null) {
          Some(income.asInstanceOf[String])
        } else {
          None
        }
      }

      val id = row.getAs(ColumnNames.ID.toString).asInstanceOf[String]
      val longitude = row.getAs(ColumnNames.LONGITUDE.toString).asInstanceOf[String]
      val latitude = row.getAs(ColumnNames.LATITUDE.toString).asInstanceOf[String]
      val reportedBy = convertToString(row.getAs(ColumnNames.REPORTED_BY.toString))//row.getAs(ColumnNames.REPORTED_BY.toString).asInstanceOf[String]
      val fallsWithin = convertToString(row.getAs(ColumnNames.FALLS_WITHIN.toString))
      val location = convertToString(row.getAs(ColumnNames.LOCATION.toString))
      val lsoaCode = convertToString(row.getAs(ColumnNames.LSOA_CODE.toString))
      val lsoaName = convertToString(row.getAs(ColumnNames.LSOA_NAME.toString))
      val crimeType = convertToString(row.getAs(ColumnNames.CRIME_TYPE.toString))
      val lastOutcome = convertToString(row.getAs(ColumnNames.LAST_OUTCOME.toString))
      val context = convertToString(row.getAs(ColumnNames.CONTEXT.toString))

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


    }).groupBy("coordinates").count().count()

    println("COUNT OF GROUPED: " + countOfGrouped)

    Nil

//      .agg(collect_list("crimeType") as "list")
      //.collectAsList().asScala.toList
  }

  private def getSparkSession(): SparkSession = {
    org.apache.spark.sql.SparkSession.builder
      .master("local")
      .appName("CrimeProcessor")
      .getOrCreate
  }
}