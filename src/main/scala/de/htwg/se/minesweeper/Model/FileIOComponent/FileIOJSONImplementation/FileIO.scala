package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOJSONImplementation

import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface
import de.htwg.se.minesweeper.Model.GameComponent.{
  GameInterface,
  Mine,
  Position
}
import java.io.{File, PrintWriter}
import play.api.libs.json.*
import scala.util.Using
import de.htwg.se.minesweeper.Config

class FileIO extends FileIOInterface {

  override def load(path: File): GameInterface = ???

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
