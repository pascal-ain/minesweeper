package de.htwg.se.minesweeper.Model

import de.htwg.se.minesweeper.Field

// The state of the game
<<<<<<< Updated upstream
final case class Game(bounds: Bounds, lost: Boolean, board: Board):
  def openField(pos: Position): Game =
=======
final case class Game(bounds: Bounds, state: State, board: Board):
  def openField(pos: Position): InsertResult =
    if !bounds.isInBounds(pos) then InsertResult.NotInBounds
    else if board.openFields.contains(pos) then InsertResult.AlreadyOpen
    else if board.flaggedFields.contains(pos) then InsertResult.AlreadyOpen
    else InsertResult.Success(canOpen(pos))

  def canOpen(pos: Position): Game =
>>>>>>> Stashed changes
    val mines = board.surroundingMines(pos, bounds)
    board.insertPosition(pos, bounds, Field.Open) match
      case None => this
      case Some(value) =>
        if board.mines.contains(pos) then copy(board = value, lost = true)
        else openSurroundingFields(value, pos, mines)

  // TODO: proud of this but looks messy
  def openSurroundingFields(fields: Board, pos: Position, mines: Int): Game =
    if mines != 0 then copy(board = fields)
    else
      def recur(
          game: Game,
          toOpen: Iterator[Position]
      ): Game =
        toOpen.nextOption() match
          case Some(value) =>
            if game.board.surroundingMines(value, bounds) != 0 then
              recur(game.openField(value), toOpen)
            else
              recur(
                game.copy(board =
                  game.board.insertPosition(value, bounds, Field.Open).get
                ),
                (toOpen ++ game.board.neighbors(value, bounds)).distinct
                  .filterNot(game.board.openFields.contains(_))
              )
          case None => game
      recur(this, board.neighbors(pos, bounds))

  def flagField(pos: Position) =
    val flaggedField = board.insertPosition(pos, bounds, Field.Flag) match
      case None        => board
      case Some(value) => value
    copy(board = flaggedField)

  override def toString() =
    (for
      y <- 0 until bounds.height
      x <- 0 until bounds.width
    yield
      val pos = Position(x, y)
      if x == bounds.width - 1 then
        whichSymbol(pos) + sys.props("line.separator")
      else whichSymbol(pos)
    ).mkString

  def whichSymbol(pos: Position) =
    board.openFields.get(pos) match
      case Some(_) if board.mines.contains(pos) => " ¤ "
      case Some(value) =>
        " " + value + " "
      case None =>
        if board.flaggedFields.contains(pos) then " F "
        else " O "

object Game:
  def apply(
      width: Int = 9,
      height: Int = 9,
      minePercentage: Double = 0.15
  ) =
    new Game(
      Bounds(width, height),
      false,
      Board(
        generateRandomPositions(
          (width * height * minePercentage).toInt,
          width,
          height
        )
      )
    )
