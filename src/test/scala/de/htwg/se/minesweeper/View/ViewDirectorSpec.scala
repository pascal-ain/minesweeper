package de.htwg.se.minesweeper.View

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class ViewDirectorSpec extends AnyWordSpec {
  "ViewDirector helps with constructing defaults and has a helper function" should {
    "provide a default set of symbols to represent the numbers" in {
      // number keypad glyph with space because this glyph is width 2
      ViewDirector.defaultNumbers(0) shouldBe "0\uFE0F\u20E3 "
      ViewDirector.defaultNumbers(5) shouldBe "5\uFE0F\u20E3 "
      ViewDirector.defaultNumbers(8) shouldBe "8\uFE0F\u20E3 "
      an[java.lang.IndexOutOfBoundsException] shouldBe thrownBy(
        ViewDirector.defaultNumbers(11)
      )
    }
  }
}
