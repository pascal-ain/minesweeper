package de.htwg.se.minesweeper.Util

import scala.collection.mutable.Stack
import scala.util.Try
import scala.util.{Either, Left => Err, Right => Ok}

implicit class StackWithOption[T](val stack: Stack[T]) {
  def popOption: Option[T] = Try(
    stack.pop()
  ).toOption // why is this not in the stdlib?
}

// For the caller to pattern match
abstract class Which
object Redo extends Which
object Undo extends Which

trait Command[T] {
  def stepOn(t: T): T
  def undoStep(t: T): T
  def redoStep(t: T): T
}

class UndoManager[T] {
  private var toUndoStack: Stack[Command[T]] = Stack.empty
  // saves history of undos, pretty much undoing undos
  private var toRedoStack: Stack[Command[T]] = Stack.empty

  def doStep(model: T, command: Command[T]): T = {
    toRedoStack =
      Stack.empty // doing something makes values in the redoStack invalid
    toUndoStack.push(command) // save the history of steps
    command.stepOn(model) // do the command and return the result
  }

  def undoStep(model: T): Either[Which, T] =
    toUndoStack.popOption match {
      case None => Err(Undo)
      case Some(command) => {
        toRedoStack.push(command)
        Ok(command.undoStep(model))
      }
    }
  def redoStep(model: T): Either[Which, T] = {
    toRedoStack.popOption match {
      case None => { Err(Redo) }
      case Some(command) => {
        toUndoStack.push(command)
        Ok(command.redoStep(model))
      }
    }
  }
}
