package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOXMLImplementation

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Config.{given}
import de.htwg.se.minesweeper.Model.GameComponent.*
import de.htwg.se.minesweeper.Config
import scala.util.Failure

class FileIOSpec extends AnyWordSpec {
  class Injector(using val init: GameInterface)
  val init = new Injector().init
  val fileIO = FileIO
  val game = init.restore(
    SnapShot(
      Map(
        Position(0, 0) -> 1,
        Position(0, 1) -> Mine
      ),
      Set(Position(6, 6)),
      Set(Position(0, 1)),
      State.Lost
    )
  )
  val gameBoard = game.getSnapShot
  val xml = fileIO.gameToXML(game)
  "A minesweeper game" should {
    "convert to XML" in {
      (xml \\ "game" \@ "width").toInt.toInt shouldBe game.getWidth
      (xml \\ "game" \@ "height").toInt shouldBe game.getHeight

      (xml \\ "openFields" \ "position")
        .map(
          fileIO.getNodePosition(_)
        ) should contain allElementsOf gameBoard.openFields.keySet

      (xml \\ "mines" \ "position")
        .map(
          fileIO.getNodePosition(_)
        ) should contain allElementsOf gameBoard.mines

      (xml \\ "flaggedFields" \ "position")
        .map(
          fileIO.getNodePosition(_)
        ) should contain allElementsOf gameBoard.flaggedFields
    }
    "be created from XML" in {
      val fromXML = fileIO.XMLToGame(xml).get
      val xmlBoard = fromXML.getSnapShot

      gameBoard.flaggedFields should contain allElementsOf xmlBoard.flaggedFields
      gameBoard.mines should contain allElementsOf xmlBoard.mines
      gameBoard.openFields should contain allElementsOf xmlBoard.openFields
      gameBoard.state shouldEqual xmlBoard.state

      fromXML.getWidth shouldEqual game.getWidth
      fromXML.getHeight shouldEqual game.getHeight
      fromXML.getSnapShot.state shouldBe gameBoard.state
    }
    "not be created from invalid XML" in {
      fileIO.XMLToGame(<invalid></invalid>) shouldBe a[Failure[_]]
      // out of bounds
      fileIO.XMLToGame(
        <game width="2" height="3">
        <openFields><position x="4" y="0"></position></openFields>
        <mines><position x="0" y="1"></position></mines>
        <flaggedFields><position x="0" y="0"></position></flaggedFields>
        </game>
      ) shouldBe a[Failure[_]]
      // intersection with flag and open
      fileIO.XMLToGame(
        <game width="2" height="3">
        <openFields><position x="0" y="0"></position></openFields>
        <mines><position x="0" y="1"></position></mines>
        <flaggedFields><position x="0" y="0"></position></flaggedFields>
        </game>
      ) shouldBe a[Failure[_]]
    }
  }
}
