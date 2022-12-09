package de.htwg.se.minesweeper.Model

trait GameInterface:
  def canOpen_?(pos: Position): InsertResult
  def openField(pos: Position): GameInterface
  def canFlag_?(pos: Position): InsertResult
  def flagField(pos: Position): GameInterface
  def whichSymbol(pos: Position): Symbols
  def getSnapShot: SnapShot
  def restore(snapshot: SnapShot): GameInterface
  def getWidth: Int
  def getHeight: Int
  def getAllPositions: Iterator[Position]
