package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Controller.Controller

trait MyApp(
    controller: Controller,
    mineSymbol: String,
    flagSymbol: String,
    closedFieldSymbol: String,
    scoreSymbols: Int => String
) extends Observer:
  def run(): Unit
