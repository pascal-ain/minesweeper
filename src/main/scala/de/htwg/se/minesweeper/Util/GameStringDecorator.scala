package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface
import de.htwg.se.minesweeper.Model.GameComponent.Position

trait GameStringDecorator(controller: ControllerInterface) {
  def symbolAt(pos: Position) = controller.symbolAt(pos)
  def decoratedSymbol(pos: Position): String
}
