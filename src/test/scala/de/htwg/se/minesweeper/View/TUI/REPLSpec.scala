package de.htwg.se.minesweeper.View.TUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Util.*
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.Controller

class REPLSpec extends AnyWordSpec {
  "The REPL" should {
    val game = Game(10, 9, 0.2)
    val repl = new REPL(game)
    "have a different string representation than the model" in {
      REPLSymbolsDecorator(
        new Controller(game),
        "💣",
        "🚩",
        "⬛",
        repl.mineCount
      ).toString shouldBe repl.game()

      repl.game() shouldNot be(game.toString)
    }
    "show surrounding mines with an emoji" in {
      repl.mineCount(5) shouldBe "5\uFE0F\u20E3" + " "
      repl.mineCount(0) shouldBe "0\uFE0F\u20E3" + " "
    }
  }
  "The REPL parses user input which" should {
    val repl = new REPL(Game(10, 9, 0.2))
    "only accept the right syntax" in {
      repl.parseInput("Hello") shouldBe None
      repl.parseInput("open this") shouldBe None
      repl.parseInput("flag") shouldBe None
      repl.parseInput("open 12.2") shouldBe None
      repl.parseInput("flag   12,3") shouldBe a[Some[
        (Position => InsertResult, Position)
      ]]
      repl.parseInput("oPen 2,3") shouldBe a[Some[
        (Position => InsertResult, Position)
      ]]
    }
  }
}
