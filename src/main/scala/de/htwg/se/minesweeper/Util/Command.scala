package de.htwg.se.minesweeper.Util

import scala.collection.mutable.Stack
import scala.util.Try

implicit class StackWithOption[T](val stack: Stack[T]):
  def popOption: Option[T] = Try(
    stack.pop()
  ).toOption // why is this not in the stdlib?

trait Command[T]:
  def stepOn(t: T): T
  // tell if something even happened
  def undoStep(t: T): T
  def redoStep(t: T): T

class UndoManager[T]:
  private var toUndoStack: Stack[Command[T]] = Stack.empty
  // saves history of undos, pretty much undoing undos
  private var toRedoStack: Stack[Command[T]] = Stack.empty

  def doStep(model: T, command: Command[T]): T =
    toRedoStack =
      Stack.empty // doing something makes previous undos in the redoStack invalid
    toUndoStack.push(command) // save the history of steps
    command.stepOn(model)

  def undoStep(model: T): Option[T] =
    toUndoStack.popOption match
      case None => None
      case Some(command) => {
        toRedoStack.push(command)
        Some(command.undoStep(model))
      }
  def redoStep(model: T): Option[T] =
    toRedoStack.popOption match
      case None => None
      case Some(command) => {
        toUndoStack.push(command)
        Some(command.redoStep(model))
      }
