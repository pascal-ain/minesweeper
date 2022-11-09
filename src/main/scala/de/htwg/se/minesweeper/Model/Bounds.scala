package de.htwg.se.minesweeper.Model

case class Bounds(width: Int, height: Int):
  def isInBounds(pos: Position) =
    if pos.x > width || pos.y > height then false
    else true

  def size = width * height
