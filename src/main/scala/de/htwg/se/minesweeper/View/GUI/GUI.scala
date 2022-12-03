package de.htwg.se.minesweeper.View.GUI

import de.htwg.se.minesweeper.Util.{MyApp, Event}
import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.*

import scala.swing._
import javax.swing.JButton
import java.awt.Rectangle
import java.awt.image.BufferedImage
import javax.swing.JComponent
import scala.swing.event.MouseClicked
import javax.swing.SwingUtilities
import java.lang.module.ModuleDescriptor.Modifier
import scala.swing.event.Key
import javax.imageio.ImageIO
import java.io.File
import javax.swing.ImageIcon
import scala.io.StdIn.readLine

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
  
  val game = controller.game
  contents = new BorderPanel {
    add(new Label("Minesweeper"), BorderPanel.Position.North)
    add(new GameBoard(game.bounds), BorderPanel.Position.Center)
  }

  pack()
  centerOnScreen()
  open()

  readLine()

  override def run() = 
    print("run")

  override def update(e: Event): Unit =
    e match
      case Event.Failure(msg) => print(msg)
      case Event.Won | Event.Lost | Event.Success => this.contents = new GameBoard(game.bounds)
  
  
  resizable = false

  class GameBoard(bounds: Bounds) extends GridPanel(bounds.width, bounds.height):
    val positions = game.board.getAllPositions
    positions.foreach(b => contents += new Field(b))
    hGap = 0
    vGap = 0


  case class Field(pos: Position) extends Button:
    // var buttonIcon: BufferedImage = ImageIO.read(new File("flag.png"))
    // buttonIcon.getScaledInstance(10, 10, 10)
    val symbol = controller.symbolAt(pos)
    text = symbol
    // symbol match
    //   case "F" => buttonIcon = ImageIO.read(new File("flag.png"))
    //     this.peer.setIcon(new ImageIcon(buttonIcon))
    //   case "B" => buttonIcon = ImageIO.read(new File("bomb.png"))
    //     this.peer.setIcon(new ImageIcon(buttonIcon))
    //   case "?" => buttonIcon = ImageIO.read(new File(""))
    //     this.peer.setIcon(new ImageIcon(buttonIcon))
    //   case _ => buttonIcon = ImageIO.read(new File(""))
    //     //which
    
    listenTo(mouse.clicks)
    reactions += {
      case evt @ MouseClicked(_,_,c,_,_) => 
        evt.peer.getButton() match
          case 1 => {
            println("left")
            controller.handleTrigger(controller.openField, pos)}
          case _ => { 
            println("right") 
            controller.handleTrigger(controller.flagField, pos)
                    }

    }
    preferredSize = new Dimension {
      width = 60
      height = 60
    }

  def handeClick(isLeftClick: Boolean, pos: Position) : Unit = {
    if isLeftClick then controller.openField(pos)
    else controller.flagField(pos)
  }

    


  
