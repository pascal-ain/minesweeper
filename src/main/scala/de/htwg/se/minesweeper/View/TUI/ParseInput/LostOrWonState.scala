package de.htwg.se.minesweeper.View.TUI.ParseInput

import scala.util.{Either, Left => Err, Right => Ok}
import de.htwg.se.minesweeper.Model.{InsertResult, Position}
import de.htwg.se.minesweeper.Util.Which
import de.htwg.se.minesweeper.Controller.Controller

case class LostOrWonState(controller: Controller)
    extends ParseState(controller):
  val helpMessage =
    s"When lost or won you can only do the following:${eol}Quitting the game with one of the following: [exit, q, quit]${eol}Undo the winning/losing move"
  override def handleSpecial(input: String) =
    Err(helpMessage)
