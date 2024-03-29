package de.htwg.se.minesweeper.Controller.ControllerComponent

import de.htwg.se.minesweeper.Model.GameComponent.{
  GameInterface,
  Symbols,
  Position
}
import de.htwg.se.minesweeper.Util.{Which, Observable}
import java.io.File
import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface

trait ControllerInterface extends Observable:
  def handleTrigger(
      handlePosition: Position => Either[String, GameInterface],
      pos: Position
  ): Unit
  def handleTrigger(undoRedo: () => Either[Which, GameInterface]): Unit
  def openField(pos: Position): Either[String, GameInterface]
  def flagField(pos: Position): Either[String, GameInterface]
  def undo(): Either[Which, GameInterface]
  def redo(): Either[Which, GameInterface]
  def symbolAt(pos: Position): Symbols
  def getAllPositions(): Iterator[Position]
  def x: Int
  def y: Int
  def newGame(width: Int, height: Int, mines: Double): Unit
  def save(path: File, implementation: FileIOInterface): Unit
  def load(path: File, implementation: FileIOInterface): Unit
