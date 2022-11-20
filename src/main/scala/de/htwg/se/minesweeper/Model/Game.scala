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
      else recursiveOpen(this, board.getSurroundingPositions(pos))
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
    else InsertResult.Success(copy(board = toggle(pos)))

  def toggle(pos: Position) =
    val flags =
      if board.flaggedFields.contains(pos) then board.flaggedFields.excl(pos)
      else board.flaggedFields.incl(pos)
    board.copy(flaggedFields = flags)

  override def toString() =
    (0 until bounds.height)
      .flatMap(posy =>
        (0 until bounds.width).map(posx =>
          if posx == bounds.width - 1 then
            whichSymbol(Position(posx, posy)) + "\n"
          else whichSymbol(Position(posx, posy))
        )
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

def won(game: Game) =
  if game.board.openFields.size == game.bounds.size - game.board.mines.size
  then game.copy(state = State.Won)
  else game
