package de.htwg.se.minesweeper.View.GUI

import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface
import de.htwg.se.minesweeper.Util.{Event, Observer}
import de.htwg.se.minesweeper.Model.GameComponent.*
import scala.swing.*
import scala.swing.event.MouseClicked
import de.htwg.se.minesweeper.Config
import javax.swing.ImageIcon
import java.io.File
import de.htwg.se.minesweeper.View.GUI.{LoadFileChooser, SaveFileChooser}

class GUI(using controller: ControllerInterface) extends Frame with Observer:
  controller.add(this)
  def gameWidth = controller.x
  def gameHeight = controller.y

  var scaleFactor = 60
  var saveDirectory = File(sys.props("user.home"))
  val loadFC = LoadFileChooser
  val saveFC = SaveFileChooser

  def run =
    title = "Minesweeper"
    resizable = false
    menuBar = new MenuBar {
      contents ++= Seq(
        new Menu("File") {
          contents ++= Seq(
            new MenuItem(Action("Save") {
              saveFC.action(this, controller)
            }),
            new MenuItem(Action("Load") {
              loadFC.action(this, controller)
            })
          )
        },
        new MenuItem(Action("New Game") {
          val newData = new GameSetter(controller, GUI.this)
        }),
        new MenuItem(Action("Undo") {
          controller.handleTrigger(controller.undo)
        }),
        new MenuItem(Action("Redo") {
          controller.handleTrigger(controller.redo)
        }),
        new MenuItem(Action("Exit") {
          sys.exit(0)
        })
      )
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
      case Event.Failure(msg) =>
        Dialog.showMessage(message = msg, title = "Error!")
      case Event.Success =>
        contents = new GameBoardNormal(gameWidth, gameHeight)
      case Event.Won =>
        contents = new GameBoardLostWon(gameWidth, gameHeight)
        showDialog(false)
      case Event.Lost =>
        contents = new GameBoardLostWon(gameWidth, gameHeight)
        showDialog(true)
      case Event.Loading(width, height, event) =>
        scaleFactor = buttonSize(width, height)
        update(event)
  override def closeOperation(): Unit =
    sys.exit(0)

  class GameBoardNormal(width: Int, height: Int)
      extends GridPanel(height, width):
    val positions =
      controller.getAllPositions().foreach(p => contents += new NormalField(p))
    hGap = 0
    vGap = 0

  class GameBoardLostWon(width: Int, height: Int)
      extends GridPanel(height, width):
    val positions =
      controller.getAllPositions().foreach(p => contents += new LostWonField(p))

  abstract class GeneralField(pos: Position) extends Button:
    this.peer.setIcon(
      resizeImage(
        new ImageIcon(
          getClass
            .getClassLoader()
            .getResource(
              "pictures/" + Config.imagePath(controller.symbolAt(pos))
            )
        )
      )
    )
    preferredSize = new Dimension {
      width = scaleFactor
      height = scaleFactor
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
      imageTest.getScaledInstance(
        scaleFactor,
        scaleFactor,
        java.awt.Image.SCALE_SMOOTH
      )
    resizeImage = new ImageIcon(imageNew)
    resizeImage
