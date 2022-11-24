package de.htwg.se.minesweeper.Model

enum InsertResult:
  case NotInBounds, AlreadyOpen, Flagged
  case Success(game: Game)
