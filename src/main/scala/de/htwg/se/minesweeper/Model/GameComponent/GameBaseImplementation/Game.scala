package de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation

import de.htwg.se.minesweeper.Model.GameComponent.{
  Position,
  GameInterface,
  InsertResult,
  Mine,
  Flag,
  Closed,
  Score,
  SnapShot,
  State
}

// The state of the game
final case class Game(bounds: Bounds, state: State, board: Board)
    extends GameInterface:
  override def canOpen_?(pos: Position): InsertResult =
    if !bounds.isInBounds(pos) then InsertResult.NotInBounds
    else if board.openFields.contains(pos) then InsertResult.AlreadyOpen
    else if board.flaggedFields.contains(pos) then InsertResult.Flagged
    else InsertResult.Ok

  override def openField(pos: Position): Game =
    val mines = board.surroundingMines(pos)
    val newGame =
      if board.mines.contains(pos) then
        copy(board = revealAllMines, state = State.Lost)
      else if mines != 0 then copy(board = updateOpenFields(pos))
      else
        recursiveOpen(
          copy(board = updateOpenFields(pos)),
          board.getSurroundingPositions(pos)
        )
    won_?(newGame)

  def recursiveOpen(game: Game, toOpen: Iterator[Position]): Game =
    toOpen.nextOption() match
      case None => game
      case Some(pos) =>
        val mines = game.board.surroundingMines(pos)
        // ignore flagged fields
        if board.flaggedFields.contains(pos) then recursiveOpen(game, toOpen)
        // field is not save to open
        else if mines != 0 then recursiveOpen(game.openField(pos), toOpen)
        else
          recursiveOpen(
            game.copy(board = game.updateOpenFields(pos)),
            toOpen
              .concat(game.board.getSurroundingPositions(pos))
              .withFilter(!game.board.openFields.contains(_))
              .distinct
          )

  def updateOpenFields(pos: Position) =
    board.copy(openFields =
      board.openFields.updated(
        pos,
        if board.mines.contains(pos) then Mine
        else board.surroundingMines(pos)
      )
    )

  def revealAllMines: Board =
    board.mines.iterator.foldLeft(board)((iteration, pos) =>
      iteration.copy(openFields = iteration.openFields.updated(pos, Mine))
    )

  override def canFlag_?(pos: Position): InsertResult =
    if !bounds.isInBounds(pos) then InsertResult.NotInBounds
    else if board.openFields.contains(pos) then InsertResult.AlreadyOpen
    else InsertResult.Ok

  override def flagField(pos: Position): Game =
    val flags =
      if board.flaggedFields.contains(pos) then board.flaggedFields.excl(pos)
      else board.flaggedFields.incl(pos)
    copy(board = board.copy(flaggedFields = flags))

  override def whichSymbol(pos: Position) =
    board.openFields.get(pos) match
      case Some(value) =>
        value match
          case _: Mine.type => Mine
          case x: Int       => Score(x)
      case None =>
        if board.flaggedFields.contains(pos) then Flag
        else Closed

  override def toString =
    board.getAllPositions
      .map(pos =>
        whichSymbol(pos).toString() + (pos match
          case Position(width, _) =>
            if width == bounds.width - 1 then sys.props("line.separator")
            else ""
        )
      )
      .mkString

  override def getWidth = bounds.width
  override def getHeight = bounds.height

  override def getSnapShot =
    SnapShot(board.openFields, board.flaggedFields, board.mines, state)
  override def restore(snapshot: SnapShot): Game =
    copy(
      board = board.copy(
        openFields = snapshot.openFields,
        mines = snapshot.mines,
        flaggedFields = snapshot.flaggedFields
      ),
      state = snapshot.state
    )
  override def getAllPositions: Iterator[Position] = board.getAllPositions

object Game:
  def apply(width: Int = 9, height: Int = 9, minePercentage: Double = 0.15) =
    new Game(
      Bounds(width, height),
      State.OnGoing,
      Board((width * height * minePercentage).toInt, Bounds(width, height))
    )
  def apply(width: Int, height: Int, mines: Int) =
    new Game(
      Bounds(width, height),
      State.OnGoing,
      Board(mines, Bounds(width, height))
    )

def won_?(game: Game): Game =
  val snapshot = game.getSnapShot
  if snapshot.openFields.size == (game.getWidth * game.getHeight) - snapshot.mines.size
  then game.restore(snapshot.copy(state = State.Won))
  else game
