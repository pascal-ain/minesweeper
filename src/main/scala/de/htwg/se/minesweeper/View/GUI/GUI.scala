package de.htwg.se.minesweeper.View.GUI

import de.htwg.se.minesweeper.Util.{Event, Observer}
import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.*

import scala.swing._
import javax.swing.ImageIcon
import scala.swing.event.MouseClicked

class GUI(controller: Controller) extends Frame with Observer with App:
  controller.add(this)

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
  val gameBounds = Bounds(controller.x, controller.y)
  contents = new BorderPanel {
    add(
      new GameBoardNormal(gameBounds),
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
      case Event.Success      => contents = new GameBoardNormal(gameBounds)
      case Event.Won =>
        contents = new GameBoardLostWon(gameBounds)
        showDialog(false)
      case Event.Lost =>
        contents = new GameBoardLostWon(gameBounds)
        showDialog(true)

  override def closeOperation(): Unit =
    sys.exit(0)

  class GameBoardNormal(bounds: Bounds)
      extends GridPanel(bounds.width, bounds.height):
    val positions =
      controller.getAllPositions().foreach(p => contents += new NormalField(p))
    hGap = 0
    vGap = 0

  class GameBoardLostWon(bounds: Bounds)
      extends GridPanel(bounds.width, bounds.height):
    val positions =
      controller.getAllPositions().foreach(p => contents += new LostWonField(p))

  abstract class GeneralField(pos: Position) extends Button:
    val symbol = controller.symbolAt(pos)
    // text = symbol
    val projectPath = System.getProperty("user.dir")
    var buttonIcon: ImageIcon = null

    symbol match
      case _: Closed.type =>
        buttonIcon = new ImageIcon(
          projectPath + "/src/main/pictures/closedField.png"
        )
      case _: Mine.type =>
        buttonIcon = new ImageIcon(
          projectPath + "/src/main/pictures/mine.png"
        )
      case _: Flag.type =>
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
