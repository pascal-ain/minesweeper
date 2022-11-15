package de.htwg.se.minesweeper.Model

enum InsertResult:
  case Success(game: Game)
  case NotInBounds
  case AlreadyOpen
  case Flagged
