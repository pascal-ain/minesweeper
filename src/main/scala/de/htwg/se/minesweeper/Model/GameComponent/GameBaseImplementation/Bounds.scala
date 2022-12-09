package de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation

import de.htwg.se.minesweeper.Model.Position

case class Bounds(width: Int, height: Int):
  def isInBounds(pos: Position) =
    if pos.x < 0 || pos.y < 0 then false
    else if pos.x >= width || pos.y >= height then false
    else true

  def size = width * height
