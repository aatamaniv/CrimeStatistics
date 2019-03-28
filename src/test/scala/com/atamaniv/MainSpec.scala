package com.atamaniv

import org.scalatest._

class MainSpec extends FlatSpec with Matchers {
  "The Hello object" should "say hello" in {
    Main.startMessage shouldBe  "Starting Crime Reports application"
  }
}
