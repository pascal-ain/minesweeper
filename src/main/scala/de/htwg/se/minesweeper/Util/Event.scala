package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Model.Game

enum Event:
  case Success(game: Game)
  case Lost, Won
  case Failure(msg: String)
