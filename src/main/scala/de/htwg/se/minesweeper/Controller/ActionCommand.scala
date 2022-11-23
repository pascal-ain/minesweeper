package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Util.Command
import de.htwg.se.minesweeper.Model.{Game, Position}

enum Action:
  case Open, Flag

final case class ActionCommand(pos: Position, action: Action)
    extends Command[Game]:
  override def stepOn(game: Game): Game = action match
    case Action.Open => game.canOpen(pos)
    case Action.Flag => game.toggleFlag(pos)
  override def undoStep(game: Game): Game = action match
    case Action.Open => game.closeField(pos)
    case Action.Flag => game.toggleFlag(pos)
  override def redoStep(game: Game): Game = action match
    case Action.Open => game.canOpen(pos)
    case Action.Flag => game.toggleFlag(pos)
