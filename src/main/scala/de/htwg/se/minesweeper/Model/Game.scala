package de.htwg.se.minesweeper.Model

import de.htwg.se.minesweeper.Field

// The state of the game
final case class Game(bounds: Bounds, state: State, board: Board):
  def openField(pos: Position): Game =
    val mines = board.surroundingMines(pos, bounds)
    board.insertPosition(pos, bounds, Field.Open) match
      case None => this
      case Some(value) =>
        if board.mines.contains(pos) then
          copy(board = value, state = State.Lost)
        else openSurroundingFields(value, pos, mines)

  def flagField(pos: Position): Game =
    board.insertPosition(pos, bounds, Field.Flag) match
      case None       => this
      case Some(flag) => copy(board = flag)

  def won_?(): Game =
    if board.openFields.size == bounds.width * bounds.height - board.mines.size
    then copy(state = State.Won)
    else this

  // TODO: proud of this but looks messy
  def openSurroundingFields(fields: Board, pos: Position, mines: Int): Game =
    if mines != 0 then copy(board = fields)
    else recursiveOpen(this, board.getSurroundingPositions(pos, bounds))

  def recursiveOpen(
      game: Game,
      toOpen: Iterator[Position]
  ): Game =
    toOpen.nextOption() match
      case Some(value) =>
        if game.board.surroundingMines(value, bounds) != 0 then
          recursiveOpen(game.openField(value), toOpen)
        else
          recursiveOpen(
            game.copy(board =
              game.board.insertPosition(value, bounds, Field.Open).get
            ),
            (toOpen ++ game.board
              .getSurroundingPositions(value, bounds)).distinct
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
      case Some(_) if board.mines.contains(pos) => " ¤ "
      case Some(value) =>
        s" $value "
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
      State.OnGoing,
      Board(
        generateRandomPositions(
          (width * height * minePercentage).toInt,
          width,
          height
        )
      )
    )
