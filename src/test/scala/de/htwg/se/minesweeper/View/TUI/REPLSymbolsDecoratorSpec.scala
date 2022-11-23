package de.htwg.se.minesweeper.View.TUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Util.*
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.Controller

class REPLSymbolsDecoratorSpec extends AnyWordSpec {
  val game = Game(10, 12, 0.2)
  "The decorator" should {
    "help with replacing the model's symbols with the caller's wanted replacements" in {
      val flagged = game.copy(board = game.toggleFlag(Position(0, 0)))
      val openStuff = Helper.openFields(
        flagged,
        flagged.board.getAllPositions
          .filterNot(pos => pos == Position(0, 0) || pos == Position(1, 1))
      )

      val controller = new Controller(openStuff)
      val decorator =
        new REPLSymbolsDecorator(controller, "l", "f", "K", (mines: Int) => "N")
      decorator.controller.toString
        .replaceAll("B", "l")
        .replaceAll("F", "f")
        .replaceAll("[0-9]", "N")
        .replaceAll("\\?", "K") shouldBe decorator.toString
    }
  }
}
