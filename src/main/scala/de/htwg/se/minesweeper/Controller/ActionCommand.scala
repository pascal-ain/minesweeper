package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Util.Command
import de.htwg.se.minesweeper.Model.{Game, Position}
import de.htwg.se.minesweeper.Model.State
import de.htwg.se.minesweeper.Model.Board

enum Action:
  case Open, Flag

final case class ActionCommand(pos: Position, action: Action)
    extends Command[Game]:
  var opened: Set[Position] = Set.empty
  override def stepOn(game: Game): Game = action match
    case Action.Open => {
      val result = game.openField(pos)
      opened = result.board.openFields.keySet.diff(game.board.openFields.keySet)
      result
    }
    case Action.Flag => game.flagField(pos)
  override def undoStep(game: Game): Game = action match
    case Action.Open => closeField(game, pos)
    case Action.Flag => game.flagField(pos)
  override def redoStep(game: Game): Game = action match
    case Action.Open => game.openField(pos)
    case Action.Flag => game.flagField(pos)

  def closeField(game: Game, pos: Position): Game =
    if game.board.mines.contains(pos) then
      game.copy(state = State.OnGoing, board = closeAllMines(game))
    else
      game.copy(
        board = opened.foldLeft[Board](game.board)((board, closeThis) =>
          board.copy(openFields = board.openFields.removed(closeThis))
        ),
        state = State.OnGoing
      )

  def closeAllMines(game: Game) =
    game.board.mines.foldLeft(game.board)((board, mine) =>
      board.copy(openFields = board.openFields.removed(mine))
    )
