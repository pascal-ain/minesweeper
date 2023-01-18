package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOTOMLImplementation

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Config.{given}
import de.htwg.se.minesweeper.Model.GameComponent.*
import toml.Value.*
import toml.*
import scala.util.Failure

class FileIOSpec extends AnyWordSpec {
  class Injector(using val init: GameInterface)
  val init = Injector().init
  val fileIO = new FileIO
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
  val toml = Toml.parse(fileIO.gameToTOML(game)).toOption.get
  val restoredGame = fileIO.TOMLtoGame(toml).get
  val gameProperties = toml.values.get("game").get.asInstanceOf[Tbl].values
  val restoredBoard = restoredGame.getSnapShot

  "A minesweeper game" should {
    "convert to TOML and vice versa" in {
      gameProperties
        .get("width")
        .get
        .asInstanceOf[Num]
        .value
        .toInt shouldBe game.getWidth
      gameProperties
        .get("height")
        .get
        .asInstanceOf[Num]
        .value
        .toInt shouldBe game.getHeight

      restoredBoard.openFields should contain allElementsOf gameBoard.openFields
      restoredBoard.mines should contain allElementsOf gameBoard.mines
      restoredBoard.flaggedFields should contain allElementsOf gameBoard.flaggedFields
      restoredBoard.state shouldBe gameBoard.state
    }
    "not be created from TOML if invalid" in {
      fileIO.TOMLtoGame(Tbl(Map("invalid" -> Num(1337)))) shouldBe a[Failure[_]]
      val outOfBounds =
        game.restore(
          SnapShot(Map.empty, Set.empty, Set(Position(99, 100)), State.OnGoing)
        )
      fileIO.TOMLtoGame(
        Toml.parse(fileIO.gameToTOML(outOfBounds)).toOption.get
      ) shouldBe a[Failure[_]]

      val interSecting = game.restore(
        SnapShot(
          Map(Position(2, 2) -> 1),
          Set(Position(2, 2)),
          Set.empty,
          State.OnGoing
        )
      )
      fileIO.TOMLtoGame(
        Toml.parse(fileIO.gameToTOML(interSecting)).toOption.get
      ) shouldBe a[Failure[_]]
    }
  }
}
