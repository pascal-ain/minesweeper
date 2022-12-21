package de.htwg.se.minesweeper.View.TUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Util.*
import de.htwg.se.minesweeper.Model.GameComponent.*
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation.*
import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.*

class REPLSymbolsDecoratorSpec extends AnyWordSpec {
  val game = Game(10, 12, 0.2)
  "The decorator" should {
    "help with replacing the model's symbols with the caller's wanted replacements" in {
      val flagPosition = Position(0, 0)
      val mine = game.board.mines.iterator.next
      val notMine =
        game.board.getAllPositions
          .filterNot(pos =>
            game.board.mines.contains(pos) || game.board.flaggedFields
              .contains(pos)
          )
          .next
      val openStuff =
        game.flagField(flagPosition).openField(mine).openField(notMine)

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
