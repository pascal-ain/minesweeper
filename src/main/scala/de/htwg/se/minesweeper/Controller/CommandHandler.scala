package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Model.*

def handleCommand(game: Game, command: Commands, pos: Position) =
  command match
    case Commands.Open    => openField(game, pos)
    case Commands.Flag    => flagField(game, pos)
    case Commands.Invalid => game

def openField(game: Game, pos: Position) =
  if game.lost then game
  else
    game.openField(pos) match
      case Some(game) => game
      case None       => game.copy(lost = true)

def flagField(game: Game, pos: Position) =
  if game.lost then game
  else game.flagField(pos)
