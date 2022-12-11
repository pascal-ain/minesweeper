package de.htwg.se.minesweeper.Controller.ControllerComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.util.{Try, Success}

class ControllerBuilderSpec extends AnyWordSpec {
  "The builder" should {
    "help with constructing a controller object that holds a game" in {
      ControllerBuilder().width(12).build() shouldBe a[Try[_]]
      ControllerBuilder()
        .width(12)
        .height(9)
        .mines(0.2)
        .build() shouldBe a[Success[_]]
    }
  }
}

class ControllerDirectorSpec extends AnyWordSpec {
  "The director" should {
    "build a default controller" in {
      ControllerDirector.defaultController().build() shouldBe a[Success[_]]
    }
  }
}
