package de.htwg.se.minesweeper.View.TUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Util.*
import de.htwg.se.minesweeper.Model.*

class REPLSpec extends AnyWordSpec {
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
