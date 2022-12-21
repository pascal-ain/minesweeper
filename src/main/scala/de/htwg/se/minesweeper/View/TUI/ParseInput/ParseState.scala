package de.htwg.se.minesweeper.View.TUI.ParseInput

import scala.util.{Either, Left => Err, Right => Ok}
import de.htwg.se.minesweeper.Model.GameComponent.{
  InsertResult,
  Position,
  GameInterface
}
import de.htwg.se.minesweeper.Util.Which
import de.htwg.se.minesweeper.Controller.ControllerInterface

enum Operation:
  case OpenOrFlag(
      function: Position => Either[String, GameInterface],
      pos: Position
  )
  case UndoRedoOrExit(function: () => Either[Which, GameInterface])

val eol = sys.props("line.separator")
abstract class ParseState(controller: ControllerInterface):
  val helpMessage: String
  def handleInput(
      userInput: String
  ): Either[String, Operation] =
    val input = userInput.trim().toLowerCase()
    if input.matches(raw"(undo|redo|exit|q|quit)") then
      input.split(raw"\s+")(0) match
        case "undo" => Ok(Operation.UndoRedoOrExit(() => controller.undo()))
        case "redo" => Ok(Operation.UndoRedoOrExit(() => controller.redo()))
        case "q" | "quit" | "exit" =>
          Ok(Operation.UndoRedoOrExit(() => sys.exit()))
        case _ => handleSpecial(input)
    else handleSpecial(input)
  def handleSpecial(input: String): Either[String, Operation]
