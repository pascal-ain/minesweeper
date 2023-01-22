package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOJSONImplementation

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Config.{given}
import de.htwg.se.minesweeper.Model.GameComponent.*
import de.htwg.se.minesweeper.Config
import scala.util.Failure
import play.api.libs.json.*

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
  val json = fileIO.gameToJSON(game)
  "A minesweeper game" should {
    "convert to JSON" in {
      (json \ "game" \ "width").get.as[Int] shouldBe game.getWidth
      (json \ "game" \ "height").get.as[Int] shouldBe game.getHeight

      (json \ "openFields").get
        .as[JsArray]
        .value
        .map(
          fileIO.getPosition(_)
        ) should contain allElementsOf gameBoard.openFields.keySet

      (json \ "flaggedFields").get
        .as[JsArray]
        .value
        .map(
          fileIO.getPosition(_)
        ) should contain allElementsOf gameBoard.flaggedFields
      (json \ "mines").get
        .as[JsArray]
        .value
        .map(
          fileIO.getPosition(_)
        ) should contain allElementsOf gameBoard.mines
    }
    "be created from JSON" in {
      val fromJSON = fileIO.JSONtoGame(json).get
      val jsonBoard = fromJSON.getSnapShot

      gameBoard.flaggedFields should contain allElementsOf jsonBoard.flaggedFields
      gameBoard.mines should contain allElementsOf jsonBoard.mines
      gameBoard.openFields should contain allElementsOf jsonBoard.openFields
      gameBoard.state shouldEqual jsonBoard.state

      fromJSON.getWidth shouldEqual game.getWidth
      fromJSON.getHeight shouldEqual game.getHeight
      fromJSON.getSnapShot.state shouldBe gameBoard.state
    }
    "not be created from invalid JSON" in {
      fileIO.JSONtoGame(Json.obj("invalid" -> 1337)) shouldBe a[Failure[_]]
      fileIO.JSONtoGame(
        Json.obj(
          "game" -> Json.obj("width" -> 9, "height" -> "10"),
          "openFields" -> Json.toJson(
            fileIO.positionsToJson(Set(Position(10, 10)))
          ),
          "flaggedFields" -> Json.toJson(fileIO.positionsToJson(Set.empty)),
          "mines" -> Json.toJson(fileIO.positionsToJson(Set.empty))
        )
      )

      fileIO.JSONtoGame(
        Json.obj(
          "game" -> Json.obj("width" -> 9, "height" -> "10"),
          "openFields" -> Json.toJson(
            fileIO.positionsToJson(Set(Position(2, 1)))
          ),
          "flaggedFields" -> Json.toJson(
            fileIO.positionsToJson(Set(Position(2, 1)))
          ),
          "mines" -> Json.toJson(fileIO.positionsToJson(Set.empty))
        )
      )
    }
  }
}
