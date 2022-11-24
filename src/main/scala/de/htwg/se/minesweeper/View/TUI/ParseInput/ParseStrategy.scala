package de.htwg.se.minesweeper.View.TUI.ParseInput

import scala.util.{Either, Left => Err, Right => Ok}
import de.htwg.se.minesweeper.Model.{InsertResult, Position}
import de.htwg.se.minesweeper.Util.Which
import de.htwg.se.minesweeper.Model.Game
import de.htwg.se.minesweeper.Controller.Controller

enum Operation:
  case OpenOrFlag(function: Position => InsertResult, pos: Position)
  case UndoRedoOrExit(function: () => Either[Which, Game])

val eol = sys.props("line.separator")
abstract class ParseStrategy(controller: Controller):
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
