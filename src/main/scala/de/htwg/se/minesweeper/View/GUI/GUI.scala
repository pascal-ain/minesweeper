package de.htwg.se.minesweeper.View.GUI

import de.htwg.se.minesweeper.Util.{MyApp, Event}
import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.Game

class GUI(
    controller: Controller,
    flagSymbol: String,
    mineSymbol: String,
    closedFieldSymbol: String,
    scoreSymbols: Int => String
) extends MyApp(
      controller,
      flagSymbol,
      mineSymbol,
      closedFieldSymbol,
      scoreSymbols
    ):
  override def run() = println("Not implemented!")
  override def update(e: Event) = println("Not implemented!")
