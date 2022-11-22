package de.htwg.se.minesweeper.Util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.Game

class GameStringDecoratorSpec extends AnyWordSpec {
  "The decorator interface has as default toString the value as the given controller" in {
    class TestDecorator(controller: Controller)
        extends GameStringDecorator(controller)

    val controller = new Controller(Game(9, 12, 0.3))
    val testDecorator = new TestDecorator(controller)
    testDecorator.toString() shouldBe controller.toString()
  }
}
