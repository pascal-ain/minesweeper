package de.htwg.se.minesweeper

import com.google.inject.Guice
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface
import de.htwg.se.minesweeper.View.GUI.GUI
import de.htwg.se.minesweeper.View.{InterfaceType, ViewFactory}

object Minesweeper {
// $COVERAGE-OFF$
  @main def main: Unit = {
    val controller = Guice
      .createInjector(new ModuleConfig)
      .getInstance(classOf[ControllerInterface])

    ViewFactory(InterfaceType.GUI, controller).run
    ViewFactory(InterfaceType.TUI, controller).run
  }
}

// $COVERAGE-ON$
