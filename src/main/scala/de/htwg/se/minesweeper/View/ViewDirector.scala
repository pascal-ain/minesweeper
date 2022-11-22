package de.htwg.se.minesweeper.View

import de.htwg.se.minesweeper.View.ViewBuilder
import de.htwg.se.minesweeper.Util.ViewType

object ViewDirector:
  def constructDefaultTUI =
    ViewBuilder()
      .viewType(ViewType.TUI)
      .width(9)
      .height(9)
      .mines(0.2)
      .flagSymbol("🚩")
      .mineSymbol("💣")
      .closedFieldSymbol("⬛")
      .scoreSymbols(defaultNumbers)
  def constructDefaultGUI =
    ViewBuilder()
      .viewType(ViewType.GUI)
      .width(9)
      .height(9)
      .mines(0.2)
      .flagSymbol("🚩")
      .mineSymbol("💣")
      .closedFieldSymbol("⬛")
      .scoreSymbols(defaultNumbers)
  def defaultNumbers(mines: Int) =
    s"${(0 until 9).map(num => s"${num.toString}\uFE0F\u20E3")(mines)} "
