package com.atamaniv

import java.util.UUID

import org.scalatest._

import scala.reflect.io.File

class MainSpec extends FlatSpec with Matchers {
  "Absolute path to folder" should "be provided" in {
    val emptyArgs: Array[String] = Array()
    Main.main(emptyArgs)
    Main.isStarted.shouldBe(false)
  }

  "Application" should "start correctly when path is provided and the folder exists" in {
    val file = File("tmp" + UUID.randomUUID()).createDirectory()
    val emptyArgs: Array[String] = Array(file.path)
    Main.main(emptyArgs)
    Main.isStarted.shouldBe(true)
    file.delete()
  }
}