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
    val file = Json.parse(Source.fromFile(path).getLines().mkString)
    JSONtoGame(file)

  def JSONtoGame(json: JsValue) =
    Try {
      val width = (json \ "game" \ "width").get.as[Int]
      val height = (json \ "game" \ "height").get.as[Int]
      val state = (json \ "game" \ "state").get.as[String] match
        case "Won"     => State.Won
        case "Lost"    => State.Lost
        case "OnGoing" => State.OnGoing
        case value =>
          throw new IOException(s"state: $value attribute is an illegal value.")

      val openFields =
        getOpenFields((json \ "openFields").get.as[JsArray].value)
      val flaggedFields =
        (json \ "flaggedFields").get.as[JsArray].value.map(getPosition(_)).toSet
      val mines =
        (json \ "mines").get.as[JsArray].value.map(getPosition(_)).toSet
      Config
        .newGame(width, height, 0)
        .restore(SnapShot(openFields, flaggedFields, mines, state))
    }

  def getOpenFields(values: Iterable[JsValue]) =
    values
      .map(value =>
        val num: Int | Mine.type =
          (value \ "value").get.as[String].toIntOption match
            case None      => Mine
            case Some(res) => res
        (getPosition(value) -> num)
      )
      .toMap

  def getPosition(value: JsValue) =
    val x = (value \ "x").get.as[Int]
    val y = (value \ "y").get.as[Int]
    Position(x, y)

  override def save(game: GameInterface): Unit =
    Using(PrintWriter(File(Config.dataPath + "/saves/game.json"))) { writer =>
      writer.write(Json.prettyPrint(gameToJSON(game)))
    }

  def gameToJSON(game: GameInterface) =
    val snapShot = game.getSnapShot
    Json.obj(
      "game" -> Json.obj(
        "width" -> game.getWidth,
        "height" -> game.getHeight,
        "state" -> snapShot.state.toString()
      ),
      "openFields" -> Json.toJson(
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

  def positionToJSON(pos: Position, num: String) =
    Json.obj("x" -> pos.x, "y" -> pos.y, "value" -> num)

  def positionsToJson(fields: Iterable[Position]) =
    fields.map(pos => Json.obj("x" -> pos.x, "y" -> pos.y))
}
