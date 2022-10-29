package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Model.*

def openField(game: Game, pos: Position) =
  if game.lost then game
  else game.openField(pos)

def flagField(game: Game, pos: Position) =
  if game.lost then game
  else game.flagField(pos)
