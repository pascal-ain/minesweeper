package de.htwg.se.minesweeper.View.TUI

import de.htwg.se.minesweeper.Util.GameStringDecorator
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import de.htwg.se.minesweeper.Model.GameComponent.{
  Mine,
  Flag,
  Closed,
  Score,
  Position
}

case class REPLSymbolsDecorator(
    controller: ControllerInterface,
    mine: String,
    flag: String,
    closed: String,
    mineCount: Int => String
) extends GameStringDecorator(controller):
  override def decoratedSymbol(pos: Position) =
    super.symbolAt(pos) match
      case Mine       => mine
      case Score(num) => mineCount(num)
      case Flag       => flag
      case Closed     => closed

  override def toString =
    val width = controller.x
    val height = controller.y
    controller
      .getAllPositions()
      .map(pos =>
        decoratedSymbol(pos) + (if pos.x == width - 1 then
                                  sys.props("line.separator")
                                else "")
      )
      .mkString
