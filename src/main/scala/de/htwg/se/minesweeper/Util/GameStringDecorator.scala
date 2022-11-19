package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Controller.Controller

trait GameStringDecorator(val controller: Controller):
  override def toString() = controller.toString()
