package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOXMLImplementation

import de.htwg.se.minesweeper.Model.GameComponent.*
import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.Game
import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface
import java.io.*
import de.htwg.se.minesweeper.Config
import scala.xml.*
import scala.util.{Using, Try, Success, Failure, Right => Ok, Left => Err}

object FileIO extends FileIOInterface {
  override def save(game: GameInterface, path: File) =
    Using(PrintWriter(path)) { writer =>
      writer.write(PrettyPrinter(100, 4).format(gameToXML(game)))
    }

  def gameToXML(game: GameInterface) =
    val snapShot = game.getSnapShot
    <game width={game.getWidth.toString()} height={game.getHeight.toString()}>
      <openFields>
      {positionsToXML(snapShot.openFields.keySet)}
      </openFields>
      <flaggedFields>
      {positionsToXML(snapShot.flaggedFields)}
      </flaggedFields>
      <mines>
      {positionsToXML(snapShot.mines)}
      </mines>
    </game>

  def positionToXML(pos: Position, value: String) =
    <position x={pos.x.toString} y={pos.y.toString}>{value}</position>

  def positionsToXML(fields: Set[Position]) =
    fields.map(positionToXML(_, ""))

  override def load(path: File) =
    Try(scala.xml.XML.loadFile(path)) match
      case Failure(exception) => Failure(exception)
      case Success(file)      => XMLToGame(file)

  def XMLToGame(xml: Elem): Try[GameInterface] =
    Try {
      def getPositionSet(field: String) =
        (xml \\ field \ "position").map(node => getNodePosition(node)).toSet
      val width = (xml \\ "game" \@ "width").toInt
      val height = (xml \\ "game" \@ "height").toInt
      val openFields = getPositionSet("openFields")
      val flaggedFields = getPositionSet("flaggedFields")
      val mines = getPositionSet("mines")
      verifyData(width, height, openFields, flaggedFields, mines) match
        case Ok(game: GameInterface) => game
        case Err(msg)                => throw new IOException(msg)
    }

  def getNodePosition(node: Node) =
    Position((node \@ "x").toInt, (node \@ "y").toInt)
}
