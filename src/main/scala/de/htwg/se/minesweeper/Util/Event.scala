package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Model.Game

enum Event:
  case Lost, Won, Success
  case Failure(msg: String)
