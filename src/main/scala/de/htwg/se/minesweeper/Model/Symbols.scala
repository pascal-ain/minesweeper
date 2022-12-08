package de.htwg.se.minesweeper.Model

trait Symbols
object Mine extends Symbols:
  override def toString(): String = "B"
object Flag extends Symbols:
  override def toString(): String = "F"
object Closed extends Symbols:
  override def toString = "?"
case class Score(num: Int) extends Symbols:
  override def toString(): String = num.toString()
