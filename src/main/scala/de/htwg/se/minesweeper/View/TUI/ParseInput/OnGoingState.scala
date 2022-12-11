package de.htwg.se.minesweeper.View.TUI.ParseInput

import scala.util.{Either, Left => Err, Right => Ok}
import de.htwg.se.minesweeper.Model.{InsertResult, Position}
import de.htwg.se.minesweeper.Util.Which
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface

case class OnGoingState(controller: ControllerInterface)
    extends ParseState(controller):
  val helpMessage =
    s"Invalid command${eol}availabe commands:${eol}open <x,y>${eol}flag <x,y>${eol}undo or redo${eol}quit, q or exit to end the game."
  override def handleSpecial(input: String) =
    if input.matches(raw"(open|flag)\s+\d+\s*,\s*\d+") then
      val operation = input.split(raw"\s+")
      val coords = input.split(raw"open|flag")(1)
      operation(0) match
        case "open" =>
          Ok(Operation.OpenOrFlag(controller.openField, getPosition(coords)))
        case "flag" =>
          Ok(Operation.OpenOrFlag(controller.flagField, getPosition(coords)))
        case _ => Err(helpMessage)
    else Err(helpMessage)
  def getPosition(str: String) =
    val coords = str.trim.split(raw"\s*,\s*").map(_.toInt)
    Position(coords(0), coords(1))
