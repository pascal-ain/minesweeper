package de.htwg.se.minesweeper.Util

enum Event:
  case Lost, Won, Success
  case Failure(msg: String)
