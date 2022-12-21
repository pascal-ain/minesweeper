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

def ViewFactory(kind: InterfaceType): ControllerInterface => ViewInterface =
  kind match {
    case InterfaceType.GUI =>
      (controller: ControllerInterface) => new GUI(controller)
    case InterfaceType.TUI =>
      (controller: ControllerInterface) =>
        new REPL(
          controller,
          flagSymbol = "[F]",
          mineSymbol = "[*]",
          closedFieldSymbol = "[?]",
          (x: Int) => s"${(0 until 9).map(num => s"[${num.toString}]")(x)}"
        )
  }
