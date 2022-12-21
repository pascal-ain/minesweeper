package de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation

import de.htwg.se.minesweeper.Util.Command
import de.htwg.se.minesweeper.Model.GameComponent.{
  State,
  GameInterface,
  SnapShot,
  Position
}

enum Action:
  case Open, Flag

final case class ActionCommand(pos: Position, action: Action)
    extends Command[GameInterface]:
  var snapShot: SnapShot =
    SnapShot(
      Map.empty,
      Set.empty,
      Set.empty,
      State.OnGoing
    )
  override def stepOn(game: GameInterface) = action match
    case Action.Open => {
      snapShot = game.getSnapShot
      game.openField(pos)
    }
    case Action.Flag => game.flagField(pos)
  override def undoStep(game: GameInterface) = action match
    case Action.Open => game.restore(snapShot)
    case Action.Flag => game.flagField(pos)
  override def redoStep(game: GameInterface) = action match
    case Action.Open => game.openField(pos)
    case Action.Flag => game.flagField(pos)
