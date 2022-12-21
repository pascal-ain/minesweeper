package de.htwg.se.minesweeper.View.TUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Model.Position
import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.*
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation.*
import de.htwg.se.minesweeper.Util.Helper
import de.htwg.se.minesweeper.Config.{given}

class REPLSpec extends AnyWordSpec {
  "The REPL is the TUI and" should {
    val game = Game(9, 10, 0.2)
    "print the things configured from the decorator" in {
      val mineSymbol = "[*]"
      val flagSymbol = "[F]"
      val closedSymbol = "[ ]"
      def scoreSymbols(x: Int) = s"[$x]"

      val flagged = game.flagField(Position(0, 0))
      val openStuff = Helper.openFields(
        using
        flagged,
        flagged.board.getAllPositions
          .filterNot(pos => pos == Position(0, 0) || pos == Position(1, 1))
      )
      val controller = new Controller(using openStuff)
      REPL(using controller).gameString() shouldBe REPLSymbolsDecorator(
        controller,
        mineSymbol,
        flagSymbol,
        closedSymbol,
        scoreSymbols
      ).toString
    }
  }
}
