package de.htwg.se.minesweeper.Model

// X, Y coordinates
case class Position(val x: Int, val y: Int):
  override def equals(pos: Any): Boolean =
    pos match
      case pos: Position => pos.x == this.x && pos.y == this.y
      case _             => false
