package de.htwg.se.minesweeper.View

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Util.ViewType
import de.htwg.se.minesweeper.View.ViewDirector
import de.htwg.se.minesweeper.View.TUI.REPL
import de.htwg.se.minesweeper.View.GUI.GUI
import scala.util.{Failure, Try, Success}
import de.htwg.se.minesweeper.Util.MyApp

class ViewBuilderSpec extends AnyWordSpec {
  "The builder" should {
    val builder = ViewBuilder()
    "let the user decide which kind of view they want" in {
      ViewDirector.constructDefaultTUI.build.get shouldBe a[REPL]
      ViewDirector.constructDefaultGUI.build.get shouldBe a[GUI]
    }
    "only let the building complete when all fields were correctly filled" in {
      ViewBuilder().build shouldBe a[Failure[Throwable]]
      ViewDirector.constructDefaultTUI.build shouldBe a[Success[MyApp]]
    }
  }
}
