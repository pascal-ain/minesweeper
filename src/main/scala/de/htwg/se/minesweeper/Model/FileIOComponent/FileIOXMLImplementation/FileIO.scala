package de.htwg.se.minesweeper.Model.FileIOComponent.FileIOXMLImplementation

import de.htwg.se.minesweeper.Model.GameComponent.{
  Position,
  Mine,
  GameInterface,
  SnapShot,
  State
}
import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.Game
import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface
import java.io.*
import de.htwg.se.minesweeper.Config
import scala.xml.*
import scala.util.{Using, Try}

class FileIO extends FileIOInterface {
  override def save(game: GameInterface) =
    Using(PrintWriter(File(Config.dataPath + "/saves/game.xml"))) { writer =>
      writer.write(PrettyPrinter(100, 4).format(gameToXML(game)))
    }

  def gameToXML(game: GameInterface) =
    val snapShot = game.getSnapShot
    <game width={game.getWidth.toString()} height={
      game.getHeight.toString()
    } state={snapShot.state.toString()}>
      <openFields>
      {openFieldsToXML(snapShot.openFields)}
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

  def openFieldsToXML(fields: Map[Position, Int | Mine.type]) =
    fields
      .map((pos: Position, at: Int | Mine.type) =>
        positionToXML(pos, at.toString())
      )

  override def load(path: File) =
    val file = scala.xml.XML.loadFile(path)
    XMLToGame(file)

  def XMLToGame(xml: Elem) =
    Try {
      def getPositionSeq(field: String) =
        xml \\ field \ "position"
      val width = (xml \\ "game" \@ "width").toInt
      val height = (xml \\ "game" \@ "height").toInt
      val state = (xml \\ "game" \@ "state") match
        case "Won"     => State.Won
        case "Lost"    => State.Lost
        case "OnGoing" => State.OnGoing
        case _ => throw new IOException("state attribute has an illegal value.")

      val openFields = getPositionSeq("openFields")
        .map(node =>
          getNodePosition(node) -> (node.text.toIntOption match
            case None      => Mine
            case Some(num) => num
          )
        )
        .collect {
          case (pos, num: Int) => (pos, num)
          case (pos, Mine)     => (pos, Mine)
        }
        .toMap[Position, Int | Mine.type]
      val flaggedFields =
        getPositionSeq("flaggedFields").map(node => getNodePosition(node))
      val mines = getPositionSeq("mines").map(node => getNodePosition(node))
      Config
        .newGame(width, height, 0.2)
        .restore(
          new SnapShot(openFields, flaggedFields.toSet, mines.toSet, state)
        )
    }

  def getNodePosition(node: Node) =
    Position((node \@ "x").toInt, (node \@ "y").toInt)
}
