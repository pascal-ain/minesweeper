package de.htwg.se.minesweeper.View

import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface
import de.htwg.se.minesweeper.View.GUI.GUI
import de.htwg.se.minesweeper.View.TUI.REPL

trait ViewInterface {
  def run: Unit
}

enum InterfaceType {
  case GUI, TUI
}

def ViewFactory(
    kind: InterfaceType,
    controller: ControllerInterface
): ViewInterface =
  kind match {
    case InterfaceType.GUI =>
      new GUI(controller)
    case InterfaceType.TUI =>
      new REPL(controller, "[*]", "[F]", "[ ]", (x: Int) => s"[$x]")
  }
