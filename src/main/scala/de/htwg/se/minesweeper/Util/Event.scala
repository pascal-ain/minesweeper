package de.htwg.se.minesweeper.Util

enum Event:
  case Lost, Won, Success
  case Failure(msg: String)
  case Loading(width: Int, height: Int, event: Event)
