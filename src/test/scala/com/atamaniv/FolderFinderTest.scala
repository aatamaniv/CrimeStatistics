package com.atamaniv

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.atamaniv.Messages.{CsvFiles, GetCsvFiles}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class FolderFinderTest() extends TestKit(ActorSystem(Main.SYSTEM_NAME)) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An FolderReader actor" must {

    "send back list of files" in {
      val echo = system.actorOf(FolderReader.props())
      echo ! GetCsvFiles("/Users/andriy/IdeaProjects/CrimeStatistics/src/test/resources/crimes")
      expectMsg(CsvFiles(List("/Users/andriy/IdeaProjects/CrimeStatistics/src/test/resources/crimes/2018-12-hampshire-street.csv",
        "/Users/andriy/IdeaProjects/CrimeStatistics/src/test/resources/crimes/2018-12-derbyshire-street.csv",
        "/Users/andriy/IdeaProjects/CrimeStatistics/src/test/resources/crimes/2018-12-hertfordshire-street.csv")))
    }

  }
}