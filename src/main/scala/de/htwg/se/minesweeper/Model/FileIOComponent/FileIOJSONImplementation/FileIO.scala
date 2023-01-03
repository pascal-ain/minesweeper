package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOJSONImplementation

import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface
import de.htwg.se.minesweeper.Model.GameComponent.{
  GameInterface,
  Mine,
  Position,
  State,
  SnapShot
}
import java.io.{File, PrintWriter, IOException}
import play.api.libs.json.*
import de.htwg.se.minesweeper.Config
import scala.io.Source
import scala.util.{Try, Using}

class FileIO extends FileIOInterface {

  override def load(path: File) =
    Try {
      val file = Json.parse(Source.fromFile(path).getLines().mkString)
      val width = (file \ "game" \ "width").get.as[Int]
      val height = (file \ "game" \ "height").get.as[Int]
      val state = (file \ "game" \ "state").get.as[String] match
        case "Won"     => State.Won
        case "Lost"    => State.Lost
        case "OnGoing" => State.OnGoing
        case value =>
          throw new IOException(s"state: $value attribute is an illegal value.")

      val openPositions = (file \ "openPositions").get
        .as[JsArray]
        .value
        .map(value =>
          val num: Int | Mine.type =
            (value \ "value").get.as[String].toIntOption match
              case None      => Mine
              case Some(res) => res
          (getPosition(value) -> num)
        )
        .toMap
      val flaggedFields =
        (file \ "flaggedFields").get.as[JsArray].value.map(getPosition(_)).toSet
      val mines =
        (file \ "mines").get.as[JsArray].value.map(getPosition(_)).toSet

      Config
        .newGame(width, height, 0)
        .restore(SnapShot(openPositions, flaggedFields, mines, state))
    }

  def getPosition(value: JsValue) =
    val x = (value \ "x").get.as[Int]
    val y = (value \ "y").get.as[Int]
    Position(x, y)
  override def save(game: GameInterface): Unit =
    val snapShot = game.getSnapShot
    val json = Json.obj(
      "game" -> Json.obj(
        "width" -> game.getWidth,
        "height" -> game.getHeight,
        "state" -> snapShot.state.toString()
      ),
      "openPositions" -> Json.toJson(
        snapShot.openFields.map((pos: Position, value: Mine.type | Int) =>
          positionToJSON(
            pos,
            value.toString
          )
        )
      ),
      "flaggedFields" -> Json.toJson(positionsToJson(snapShot.flaggedFields)),
      "mines" -> Json.toJson(positionsToJson(snapShot.mines))
    )
    Using(PrintWriter(File(Config.dataPath + "/saves/game.json"))) { writer =>
      writer.write(Json.prettyPrint(json))
    }

  def positionToJSON(pos: Position, num: String) =
    Json.obj("x" -> pos.x, "y" -> pos.y, "value" -> num)

  def positionsToJson(fields: Iterable[Position]) =
    fields.map(pos => Json.obj("x" -> pos.x, "y" -> pos.y))
}
