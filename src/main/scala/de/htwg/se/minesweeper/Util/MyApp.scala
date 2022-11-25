package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Model.Game

trait MyApp(
    game: Game,
    mineSymbol: String,
    flagSymbol: String,
    closedFieldSymbol: String,
    scoreSymbols: Int => String
) extends Observer:
  def run(): Unit
