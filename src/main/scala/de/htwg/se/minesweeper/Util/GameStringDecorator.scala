package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.Position

trait GameStringDecorator(controller: Controller):
  def symbolAt(pos: Position) = controller.symbolAt(pos)
  def decoratedSymbol(pos: Position): String
