package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.Position

trait GameStringDecorator(controller: Controller):
  override def toString() = controller.toString()
  def symbolAt(pos: Position) = controller.symbolAt(pos)
