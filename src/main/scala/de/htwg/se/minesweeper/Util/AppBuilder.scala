package de.htwg.se.minesweeper.Util

import scala.util.Try

enum ViewType:
  case TUI, GUI

trait AppBuilder:
  def viewType(kind: ViewType): AppBuilder
  def width(x: Int): AppBuilder
  def height(y: Int): AppBuilder
  def mines(percentage: Double): AppBuilder
  def flagSymbol(flag: String): AppBuilder
  def mineSymbol(mine: String): AppBuilder
  def closedFieldSymbol(closed: String): AppBuilder
  def scoreSymbols(conv: Int => String): AppBuilder
  def build: Try[MyApp]
