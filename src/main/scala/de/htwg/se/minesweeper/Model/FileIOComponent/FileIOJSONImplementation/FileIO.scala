package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOJSONImplementation

import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface
import de.htwg.se.minesweeper.Model.GameComponent.*
import java.io.{File, PrintWriter, IOException}
import play.api.libs.json.*
import de.htwg.se.minesweeper.Config
import scala.io.Source
import scala.util.{Try, Using, Left => Err, Right => Ok, Success, Failure}

class FileIO extends FileIOInterface {

  override def load(path: File) =
    Try(Json.parse(Source.fromFile(path).getLines().mkString)) match
      case Failure(exception) => Failure(exception)
      case Success(file)      => JSONtoGame(file)

  def JSONtoGame(json: JsValue) =
    def getPositionSet(field: String) =
      (json \ field).get.as[JsArray].value.map(getPosition(_)).toSet
    Try {
      val width = (json \ "game" \ "width").get.as[Int]
      val height = (json \ "game" \ "height").get.as[Int]
      val openFields = getPositionSet("openFields")
      val flaggedFields = getPositionSet("flaggedFields")
      val mines = getPositionSet("mines")
      verifyData(width, height, openFields, flaggedFields, mines) match
        case Err(msg)  => throw new IOException(msg)
        case Ok(value) => value

    }

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
        "height" -> game.getHeight
      ),
      "openFields" -> Json.toJson(positionsToJson(snapShot.openFields.keySet)),
      "flaggedFields" -> Json.toJson(positionsToJson(snapShot.flaggedFields)),
      "mines" -> Json.toJson(positionsToJson(snapShot.mines))
    )

  def positionToJSON(pos: Position, num: String) =
    Json.obj("x" -> pos.x, "y" -> pos.y, "value" -> num)

  def positionsToJson(fields: Iterable[Position]) =
    fields.map(pos => Json.obj("x" -> pos.x, "y" -> pos.y))
}
