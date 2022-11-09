package de.htwg.se.minesweeper.View.TUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Util.*
import de.htwg.se.minesweeper.Model.Game

class REPLSpec extends AnyWordSpec {
  "The REPL parses user input which" should {
    val repl = new REPL(Game(10, 9, 0.2))
    "only accept the right syntax" in {
      repl.parseInput("Hello") shouldBe None
      repl.parseInput("open this") shouldBe None
      repl.parseInput("flag") shouldBe None
      repl.parseInput("open 12.2") shouldBe None
      repl.parseInput("flag   12,3") should not be None
      repl.parseInput("open 2,3") should not be Some
    }
    "split them accordingly to get the meaning out of them" in {
      repl.handleTokens(Array("open", "12,44")) should not be None
      repl.handleTokens(Array("this", "cant", "be")) shouldBe None
      repl.handleTokens(Array("flag", "12,44")) should not be None
      repl.insertPosition(Array("no")) shouldBe None
    }
  }
}
