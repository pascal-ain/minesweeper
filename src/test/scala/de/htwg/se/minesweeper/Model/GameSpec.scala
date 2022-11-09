package de.htwg.se.minesweeper.Model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Util.*

class GameSpec extends AnyWordSpec {
  val eol = sys.props("line.separator")
  val notOpened = "[?]"
  val mineSymbol = " ¤ "
  val flagSymbol = " F "
  def calculateMines(width: Int, height: Int, percentage: Double) =
    (width * height * percentage).toInt
  val game1 = Game(2, 4, 0.15)
  val game2 = Game(4, 2, 0.30)
  val game3 = Game(4, 4, 0.50)
  "The minesweeper game" should {
    "have a scalable width" in {
      game1.toString should startWith(s"$notOpened$notOpened$eol")
      game2.toString should startWith(
        s"$notOpened$notOpened$notOpened$notOpened$eol"
      )
      game3.toString() should startWith(
        s"$notOpened$notOpened$notOpened$notOpened$eol"
      )
    }
    "have a scalable height" in {
      val regexClosed = """\[\?\]""".r
      game1.toString should fullyMatch regex (s"($regexClosed$regexClosed$eol){4}")
      game2.toString should fullyMatch regex (s"($regexClosed$regexClosed$regexClosed$regexClosed$eol){2}")
      game3
        .toString() should fullyMatch regex (s"($regexClosed$regexClosed$regexClosed$regexClosed$eol){4}")
    }
    "have a scalable minecount" in {
      game1.board.mines.size shouldBe calculateMines(2, 4, 0.15)
      game2.board.mines.size shouldBe calculateMines(4, 2, 0.30)
      game3.board.mines.size shouldBe calculateMines(4, 4, 0.50)
    }
    "only have mines populated when created" in {
      game1.board.mines should not be empty
      game2.board.flaggedFields shouldBe empty
      game3.board.openFields shouldBe empty
    }
    "be ongoing (not lost) when created" in {
      game1.state shouldBe State.OnGoing
      game2.state shouldBe State.OnGoing
      game3.state shouldBe State.OnGoing
    }
  }
  "A symbol on the field" should {
    val notRevealed = Position(1, 1)
    s"be '$notOpened' when unopened" in {
      game1.whichSymbol(notRevealed) shouldBe notOpened
      game2.whichSymbol(notRevealed) shouldBe notOpened
    }
    s"be a '$mineSymbol' when the opened field was a mine" in {
      val mine1 = game1.board.mines.toVector(0)
      game1.canOpen(mine1).whichSymbol(mine1) shouldBe mineSymbol
      val mine2 = game2.board.mines.toVector(0)
      game2.canOpen(mine2).whichSymbol(mine2) shouldBe mineSymbol
    }
    "be the number of surrounding mines when it is revealed" in {
      val safeFields1 =
        Helper.getAllPositions(game1).filterNot(game1.board.mines.contains(_))
      val open1 = safeFields1.next()
      game1
        .canOpen(open1)
        .whichSymbol(open1) shouldBe s" ${game1.board.surroundingMines(open1, game1.bounds).toString} "
      val safeFields2 =
        Helper.getAllPositions(game2).filterNot(game2.board.mines.contains(_))
      val open2 = safeFields2.next
      game2
        .canOpen(open2)
        .whichSymbol(open2) shouldBe s" ${game2.board.surroundingMines(open2, game2.bounds)} "
    }
    s"be a '$flagSymbol' when it got flagged" in {
      val flagField = Position(1, 1)
      game1
        .copy(board = game1.toggle(flagField))
        .whichSymbol(flagField) shouldBe flagSymbol
      game2
        .copy(board = game2.toggle(flagField))
        .whichSymbol(flagField) shouldBe flagSymbol
    }
  }
  "Opening fields" should {
    "handle error and success" in {
      game1.openField(Position(1, 1)) shouldBe a[InsertResult.Success]
      game1.openField(Position(420, 1337)) shouldBe InsertResult.NotInBounds
      game1
        .canOpen(Position(0, 0))
        .openField(Position(0, 0)) shouldBe InsertResult.AlreadyOpen
    }
    "with 0 surrounding mines open its neighbors" in {
      val bigGame = Game(20, 20, 0.1)
      val safeField = Helper
        .getAllPositions(bigGame)
        .filter(bigGame.board.surroundingMines(_, bigGame.bounds) == 0)
        .next()
      bigGame.canOpen(safeField).board.openFields.size should be >= 3
    }
  }
  "Flagging fields" should {
    "handle error and success" in {
      game1.flagField(Position(1, 1)) shouldBe a[InsertResult.Success]
      game2.flagField(Position(142, 1337)) shouldBe InsertResult.NotInBounds
      game3
        .canOpen(Position(1, 1))
        .flagField(Position(1, 1)) shouldBe InsertResult.AlreadyOpen
    }
    "has a toggling behaviour, flagged fields will get unflagged vice versa" in {
      game1
        .copy(board = game1.toggle(Position(1, 1)))
        .board
        .flaggedFields
        .size shouldBe 1
      game2
        .copy(board = game2.toggle(Position(1, 2)))
        .toggle(Position(1, 2))
        .flaggedFields
        .size shouldBe 0
    }
  }
  "The state of the game" should {
    val bigGame = Game(80, 20, 0.15)
    val noMines = Helper
      .getAllPositions(bigGame)
      .filterNot(bigGame.board.mines.contains(_))
    val wonGame = Helper.openFields(bigGame, noMines)

    val notMines =
      Helper.getAllPositions(game1).filterNot(game1.board.mines.contains(_))
    val lostGame = game2
      .canOpen(game2.board.mines.toVector(0))
    "be no fields to open when won" in {
      Helper
        .getAllPositions(wonGame)
        .filter(pos =>
          !wonGame.board.mines.contains(pos)
            && !wonGame.board.openFields.contains(pos)
        )
        .toVector
        .size shouldBe 0
    }
    "be won when no fields are left to open" in {
      won_?(wonGame).state shouldBe State.Won
    }
    "change to lost when a mine was opened" in {
      lostGame.state shouldBe State.Lost
    }
    "reveal all mines when lost" in {
      lostGame.board.mines.forall(lostGame.board.openFields.contains(_))
    }
    "be neither lost or won when no mines are opened and there are still fields to open" in {
      val notMines =
        Helper.getAllPositions(game1).filterNot(game1.board.mines.contains(_))
      game1.canOpen(notMines.next()).state shouldBe State.OnGoing
    }
  }
}
