package de.htwg.se.minesweeper.View.GUI

import de.htwg.se.minesweeper.Controller.ControllerInterface
import de.htwg.se.minesweeper.Util.{Event, Observer}
import de.htwg.se.minesweeper.Model.GameComponent.*
import scala.swing._
import javax.swing.ImageIcon
import scala.swing.event.MouseClicked

class GUI(using controller: ControllerInterface) extends Frame with Observer:
  controller.add(this)
  val gameWidth = controller.x
  val gameHeight = controller.y

  def run =
    title = "Minesweeper"
    resizable = false
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Undo") {
          controller.handleTrigger(controller.undo)
        })
        contents += new MenuItem(Action("Redo") {
          controller.handleTrigger(controller.redo)
        })
        contents += new MenuItem(Action("Exit") {
          sys.exit(0)
        })
      }
    }
    contents = new BorderPanel {
      add(
        new GameBoardNormal(gameWidth, gameHeight),
        BorderPanel.Position.Center
      )
    }
    pack()
    centerOnScreen()
    open()

  def showDialog(lost: Boolean) =
    val msg = if lost then "You lost!" else "You won!"
    Dialog.showMessage(message = msg, title = "Game Status")
  override def update(e: Event): Unit =
    e match
      case Event.Failure(msg) => println(msg)
      case Event.Success =>
        contents = new GameBoardNormal(gameWidth, gameHeight)
      case Event.Won =>
        contents = new GameBoardLostWon(gameWidth, gameHeight)
        showDialog(false)
      case Event.Lost =>
        contents = new GameBoardLostWon(gameWidth, gameHeight)
        showDialog(true)

  override def closeOperation(): Unit =
    sys.exit(0)

  class GameBoardNormal(width: Int, height: Int)
      extends GridPanel(width, height):
    val positions =
      controller.getAllPositions().foreach(p => contents += new NormalField(p))
    hGap = 0
    vGap = 0

  class GameBoardLostWon(width: Int, height: Int)
      extends GridPanel(width, height):
    val positions =
      controller.getAllPositions().foreach(p => contents += new LostWonField(p))

  abstract class GeneralField(pos: Position) extends Button:
    val symbol = controller.symbolAt(pos)
    // text = symbol
    val projectPath = System.getProperty("user.dir")
    var buttonIcon: ImageIcon = null

    symbol match
      case Closed =>
        buttonIcon = new ImageIcon(
          projectPath + "/src/main/pictures/closedField.png"
        )
      case Mine =>
        buttonIcon = new ImageIcon(
          projectPath + "/src/main/pictures/mine.png"
        )
      case Flag =>
        buttonIcon = new ImageIcon(
          projectPath + "/src/main/pictures/flag.png"
        )
      case Score(num) =>
        buttonIcon = new ImageIcon(
          s"${projectPath}/src/main/pictures/${num}.png"
        )

    this.peer.setIcon(resizeImage(buttonIcon))
    preferredSize = new Dimension {
      width = 60
      height = 60
    }

  case class NormalField(pos: Position) extends GeneralField(pos: Position):
    listenTo(mouse.clicks)
    reactions += { case evt @ MouseClicked(_, _, c, _, _) =>
      evt.peer.getButton() match
        case 1 => {
          controller.handleTrigger(controller.openField, pos)
        }
        case _ => {
          controller.handleTrigger(controller.flagField, pos)
        }
    }

  case class LostWonField(pos: Position) extends GeneralField(pos: Position):
    listenTo(mouse.clicks)
    reactions += { case MouseClicked(_, _, _, _, _) =>
      println("Game finished")
    }

  def resizeImage(image: ImageIcon) =
    var resizeImage = image
    var imageTest = image.getImage()
    var imageNew =
      imageTest.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH)
    resizeImage = new ImageIcon(imageNew)
    resizeImage
