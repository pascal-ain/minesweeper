package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOTOMLImplementation

import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface
import java.io.{File, PrintWriter, IOException}
import de.htwg.se.minesweeper.Config
import de.htwg.se.minesweeper.Model.GameComponent.*
import toml.Node.NamedTable
import toml.Value.*
import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.Game
import toml.*
import scala.util.{Try, Using, Failure, Success, Left => Err, Right => Ok}
import java.nio.file.{Files, Path}

class FileIO extends FileIOInterface {
  override def save(game: GameInterface) = {
    Using(PrintWriter(File(Config.dataPath + "/saves/game.toml"))) { writer =>
      writer.write(gameToTOML(game))
    }
  }

  def gameToTOML(game: GameInterface): String = {
    val snapShot: SnapShot = game.getSnapShot
    val gameProperties =
      NamedTable(
        List("game"),
        List(
          "width" -> Num(game.getWidth),
          "height" -> Num(game.getWidth)
        )
      )
    val mines = positionsToTOML("mines", snapShot.mines)
    val flags = positionsToTOML("flaggedFields", snapShot.flaggedFields)
    val openFields = positionsToTOML("openFields", snapShot.openFields.keySet)

    Toml.generate(Root(List(gameProperties, openFields, flags, mines)))
  }

  def positionsToTOML(table: String, positions: Set[Position]) = {
    NamedTable(
      List(table),
      List(
        "positions" -> Arr(
          positions
            .map(pos => Tbl(Map("x" -> Num(pos.x), "y" -> Num(pos.y))))
            .toList
        )
      )
    )
  }

  override def load(path: File) = {
    Toml.parse(Files.readString(Path.of(path.toString))) match
      case Err(exception) => Failure(new IOException("Invalid file!"))
      case Ok(value)      => TOMLtoGame(value)
  }

  def TOMLtoGame(file: Tbl): Try[GameInterface] = {
    val data = file.values
    def getPositions(which: String) = {
      data
        .get(which)
        .get
        .asInstanceOf[Tbl]
        .values
        .get("positions")
        .get
        .asInstanceOf[Arr]
        .values
        .toSet
        .map(table => {
          val tbl = table.asInstanceOf[Tbl].values
          val x = tbl.get("x").get.asInstanceOf[Num].value.toInt
          val y = tbl.get("y").get.asInstanceOf[Num].value.toInt
          Position(x, y)
        })
    }
    Try {
      val gameProperties = data.get("game").get.asInstanceOf[Tbl].values
      val width = gameProperties.get("width").get.asInstanceOf[Num].value.toInt
      val height =
        gameProperties.get("height").get.asInstanceOf[Num].value.toInt
      val mines = getPositions("mines")
      val openFields = getPositions("openFields")
      val flags = getPositions("flaggedFields")
      verifyData(width, height, openFields, flags, mines) match
        case Err(msg)  => throw new IOException(msg)
        case Ok(value) => value
    }
  }

}
