package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Model.*

def openField(game: Game, pos: Position) =
  game.state match
    case State.OnGoing => game.openField(pos)
    case State.Lost    => game
    case State.Won     => game

def flagField(game: Game, pos: Position) =
  game.state match
    case State.OnGoing => game.flagField(pos)
    case State.Lost    => game
    case State.Won     => game
