package de.htwg.se.minesweeper.View.GUI

import de.htwg.se.minesweeper.Util.{MyApp, Event}
import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.*

import scala.swing._
import javax.swing.JButton
import java.awt.Rectangle
import java.awt.image.BufferedImage
import javax.swing.JComponent
import scala.swing.event._
import javax.imageio.ImageIO
import java.io.File
import javax.swing.ImageIcon
import scala.io.StdIn.readLine
import javax.swing.WindowConstants
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.plaf.OptionPaneUI
import javax.swing.JOptionPane
import scala.swing.Swing.EmptyIcon

class GUI(
    controller: Controller,
    flagSymbol: String,
    mineSymbol: String,
    closedFieldSymbol: String,
    scoreSymbols: Int => String
) extends Frame
    with MyApp(
      controller,
      flagSymbol,
      mineSymbol,
      closedFieldSymbol,
      scoreSymbols
    ):

  controller.add(this)

  title = "Minesweeper"
  resizable = false

  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("New Game") {
        // controller.setGame()
      })
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

  val game = controller.game
  contents = new BorderPanel {
    add(new GameBoardNormal(game.bounds), BorderPanel.Position.Center)
  }

  pack()
  centerOnScreen()
  open()
  readLine()

  override def run() =
    print("run")

  def showDialog(lost: Boolean) =
    if lost then
      Dialog.showConfirmation(
        contents.head,
        "New Game",
        optionType = Dialog.Options.YesNo,
        title = "Game Lost"
      )
    else
      Dialog.showConfirmation(
        contents.head,
        "New Game",
        optionType = Dialog.Options.YesNo,
        title = "Game Won"
      )

  override def update(e: Event): Unit =
    e match
      case Event.Failure(msg) => print(msg)
      case Event.Success      => contents = new GameBoardNormal(game.bounds)
      case Event.Won =>
        contents = new GameBoardLostWon(game.bounds)
        showDialog(false)
      case Event.Lost =>
        contents = new GameBoardLostWon(game.bounds)
        showDialog(true)

  override def closeOperation(): Unit =
    sys.exit(0)

  class GameBoardNormal(bounds: Bounds)
      extends GridPanel(bounds.width, bounds.height):
    val positions =
      game.board.getAllPositions.foreach(p => contents += new NormalField(p))
    hGap = 0
    vGap = 0

  class GameBoardLostWon(bounds: Bounds)
      extends GridPanel(bounds.width, bounds.height):
    val positions =
      game.board.getAllPositions.foreach(p => contents += new LostWonField(p))

  abstract class GeneralField(pos: Position) extends Button:
    val symbol = controller.symbolAt(pos)
    // text = symbol
    val projectPath = System.getProperty("user.dir")
    var buttonIcon: ImageIcon = null

    symbol match
      case "?" =>
        buttonIcon = new ImageIcon(
          projectPath + "\\src\\main\\pictures\\closedField.png"
        )
        this.peer.setIcon(resizeImage(buttonIcon))
      case "B" =>
        buttonIcon = new ImageIcon(
          projectPath + "\\src\\main\\pictures\\mine.png"
        )
        this.peer.setIcon(resizeImage(buttonIcon))
      case "F" =>
        buttonIcon = new ImageIcon(
          projectPath + "\\src\\main\\pictures\\flag.png"
        )
        this.peer.setIcon(resizeImage(buttonIcon))
      case "0" =>
        buttonIcon = new ImageIcon(
          projectPath + "\\src\\main\\pictures\\0.png"
        )
        this.peer.setIcon(resizeImage(buttonIcon))
      case "1" =>
        buttonIcon = new ImageIcon(projectPath + "\\src\\main\\pictures\\1.png")
        this.peer.setIcon(resizeImage(buttonIcon))
      case "2" =>
        buttonIcon = new ImageIcon(projectPath + "\\src\\main\\pictures\\2.png")
        this.peer.setIcon(resizeImage(buttonIcon))
      case "3" =>
        buttonIcon = new ImageIcon(projectPath + "\\src\\main\\pictures\\3.png")
        this.peer.setIcon(resizeImage(buttonIcon))
      case "4" =>
        buttonIcon = new ImageIcon(projectPath + "\\src\\main\\pictures\\4.png")
        this.peer.setIcon(resizeImage(buttonIcon))
      case "5" =>
        buttonIcon = new ImageIcon(projectPath + "\\src\\main\\pictures\\5.png")
        this.peer.setIcon(resizeImage(buttonIcon))
      case "6" =>
        buttonIcon = new ImageIcon(projectPath + "\\src\\main\\pictures\\6.png")
        this.peer.setIcon(resizeImage(buttonIcon))
      case "7" =>
        buttonIcon = new ImageIcon(projectPath + "\\src\\main\\pictures\\7.png")
        this.peer.setIcon(resizeImage(buttonIcon))
      case "8" =>
        buttonIcon = new ImageIcon(projectPath + "\\src\\main\\pictures\\8.png")
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
