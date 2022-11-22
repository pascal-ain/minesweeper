package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Controller.Controller

trait GameStringDecorator(controller: Observable):
  override def toString() = controller.toString()
