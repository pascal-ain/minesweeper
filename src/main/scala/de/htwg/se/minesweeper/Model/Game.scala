package de.htwg.se.minesweeper.Model

import de.htwg.se.minesweeper.Field

// The state of the game
final case class Game(bounds: Bounds, state: State, board: Board):
  def openField(pos: Position): InsertResult =
    if !bounds.isInBounds(pos) then InsertResult.NotInBounds
    else if board.openFields.contains(pos) then InsertResult.AlreadyOpen
    else openEvent(pos)

  def openEvent(pos: Position): InsertResult.Success =
    if board.mines.contains(pos) then
      InsertResult.Success(copy(board = revealAllMines, state = State.Lost))
    else
      val opened =
        open(pos, board.surroundingMines(pos, bounds))
      InsertResult.Success(won_?(opened))

  def won_?(game: Game) =
    if game.board.openFields.size == game.bounds.size - game.board.mines.size
    then game.copy(state = State.Won)
    else game

  def revealAllMines: Board =
    board.mines.iterator
      .foldLeft(board)((iteration, pos) =>
        iteration.copy(openFields = iteration.openFields + (pos -> "¤"))
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

  def open(pos: Position, mines: Int): Game =
    if mines != 0 then
      copy(board = board.copy(openFields = board.openFields + (pos -> mines)))
    else recursiveOpen(this, board.getSurroundingPositions(pos, bounds))

  // TODO: simplify this
  def recursiveOpen(
      game: Game,
      toOpen: Iterator[Position]
  ): Game =
    toOpen.nextOption() match
      case Some(pos) =>
        val mines = game.board.surroundingMines(pos, game.bounds)
        if mines != 0 then
          recursiveOpen(
            game.open(pos, mines),
            toOpen
          )
        else
          recursiveOpen(
            game.copy(board =
              game.board
                .copy(openFields = game.board.openFields + (pos -> mines))
            ),
            toOpen ++ game.board
              .getSurroundingPositions(pos, bounds)
              .distinct
              .filterNot(game.board.openFields.contains(_))
          )
      case None => game

  override def toString() =
    (0 until bounds.height)
      .flatMap(posy =>
        (0 until bounds.width).map(posx =>
          if posx == bounds.width - 1 then
            whichSymbol(Position(posx, posy)) + sys.props("line.separator")
          else whichSymbol(Position(posx, posy))
        )
      )
      .mkString

  def whichSymbol(pos: Position) =
    board.openFields.get(pos) match
      case Some(value) =>
        s" $value "
      case None =>
        if board.flaggedFields.contains(pos) then " F "
        else "[?]"

object Game:
  def apply(
      width: Int = 9,
      height: Int = 9,
      minePercentage: Double = 0.15
  ) =
    new Game(
      Bounds(width, height),
      State.OnGoing,
      Board(
        generateRandomPositions(
          (width * height * minePercentage).toInt,
          width,
          height
        )
      )
    )
