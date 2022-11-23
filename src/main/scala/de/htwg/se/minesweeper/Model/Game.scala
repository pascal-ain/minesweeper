package de.htwg.se.minesweeper.Model

// The state of the game
final case class Game(bounds: Bounds, state: State, board: Board):
  def openField(pos: Position): InsertResult =
    if !bounds.isInBounds(pos) then InsertResult.NotInBounds
    else if board.openFields.contains(pos) then InsertResult.AlreadyOpen
    else if board.flaggedFields.contains(pos) then InsertResult.Flagged
    else InsertResult.Success(canOpen(pos))

  def canOpen(pos: Position): Game =
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
    won(newGame)

  def recursiveOpen(game: Game, toOpen: Iterator[Position]): Game =
    toOpen.nextOption() match
      case None => game
      case Some(pos) =>
        val mines = game.board.surroundingMines(pos)
        if mines != 0 then recursiveOpen(game.canOpen(pos), toOpen)
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
        if board.mines.contains(pos) then 'B'
        else board.surroundingMines(pos)
      )
    )

  def revealAllMines: Board =
    board.mines.iterator.foldLeft(board)((iteration, pos) =>
      iteration.copy(openFields = iteration.openFields.updated(pos, 'B'))
    )

  def flagField(pos: Position) =
    if !bounds.isInBounds(pos) then InsertResult.NotInBounds
    else if board.openFields.contains(pos) then InsertResult.AlreadyOpen
    else InsertResult.Success(toggleFlag(pos))

  def toggleFlag(pos: Position) =
    val flags =
      if board.flaggedFields.contains(pos) then board.flaggedFields.excl(pos)
      else board.flaggedFields.incl(pos)
    copy(board = board.copy(flaggedFields = flags))

  def updateClosedFields(pos: Position) =
    board.copy(openFields = board.openFields.removed(pos))

  def closeField(pos: Position) =
    val mines = board.surroundingMines(pos)
    val newGame =
      if board.mines.contains(pos) then copy(board = closeAllMines)
      else if mines != 0 then copy(board = updateClosedFields(pos))
      else
        recursiveClose(
          copy(board = updateClosedFields(pos)),
          board.getSurroundingPositions(pos)
        )
    newGame.copy(state = State.OnGoing)

  def recursiveClose(game: Game, toClose: Iterator[Position]): Game =
    toClose.nextOption() match
      case None => game
      case Some(pos) =>
        val mines = game.board.surroundingMines(pos)
        if mines != 0 then recursiveClose(game.closeField(pos), toClose)
        else
          recursiveClose(
            game.copy(board = game.updateClosedFields(pos)),
            toClose
              .concat(game.board.getSurroundingPositions(pos))
              .withFilter(game.board.openFields.contains(_))
              .distinct
          )

  def closeAllMines: Board =
    board.mines.iterator.foldLeft(board)((iteration, pos) =>
      iteration.copy(openFields = iteration.openFields.removed(pos))
    )

  override def toString() =
    board.getAllPositions
      .map(pos =>
        if pos.x == bounds.width - 1 then whichSymbol(pos) + "\n"
        else whichSymbol(pos)
      )
      .mkString

  def whichSymbol(pos: Position) =
    board.openFields.get(pos) match
      case Some(value) =>
        s"$value"
      case None =>
        if board.flaggedFields.contains(pos) then "F"
        else "?"

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

def won(game: Game) =
  if game.board.openFields.size == game.bounds.size - game.board.mines.size
  then game.copy(state = State.Won)
  else game
