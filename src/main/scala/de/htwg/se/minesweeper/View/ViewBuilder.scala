package de.htwg.se.minesweeper.View

import de.htwg.se.minesweeper.Util.{MyApp, AppBuilder, ViewType}
import scala.util.{Try, Failure, Success}
import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.Game
import de.htwg.se.minesweeper.View.TUI.REPL
import de.htwg.se.minesweeper.View.GUI.GUI

case class ViewBuilder(
    interface: Option[ViewType],
    width: Option[Int],
    height: Option[Int],
    mines: Option[Double],
    flagSymbol: Option[String],
    mineSymbol: Option[String],
    closedFieldSymbol: Option[String],
    scoreSymbols: Option[Int => String]
) extends AppBuilder:
  override def viewType(kind: ViewType) = copy(interface = Some(kind))
  override def width(x: Int) = copy(width = Some(x + 1))
  override def height(y: Int) = copy(height = Some(y + 1))
  override def mines(num: Double) =
    require(num < 1)
    copy(mines = Some(num))

  override def flagSymbol(flag: String) = copy(flagSymbol = Some(flag))
  override def mineSymbol(mine: String) = copy(mineSymbol = Some(mine))
  override def closedFieldSymbol(closed: String) =
    copy(closedFieldSymbol = Some(closed))
  override def scoreSymbols(conv: Int => String) =
    copy(scoreSymbols = Some(conv))
  override def build: Try[MyApp] =
    (
      interface,
      width,
      height,
      mines,
      flagSymbol,
      mineSymbol,
      closedFieldSymbol,
      scoreSymbols
    ) match
      case (
            Some(view),
            Some(width),
            Some(height),
            Some(mines),
            Some(flagSymbol),
            Some(mineSymbol),
            Some(closedFieldSymbol),
            Some(scoreSymbols)
          ) =>
        view match
          case ViewType.TUI =>
            Success(
              new REPL(
                new Controller(
                  Game(width, height, (width * height * mines).toInt)
                ),
                mineSymbol,
                flagSymbol,
                closedFieldSymbol,
                scoreSymbols
              )
            )
          case ViewType.GUI =>
            Success(
              new GUI(
                new Controller(
                  Game(width, height, (width * height * mines).toInt)
                ),
                mineSymbol,
                flagSymbol,
                closedFieldSymbol,
                scoreSymbols
              )
            )
      case _ => Failure(IllegalStateException("All fields need to be filled!"))

object ViewBuilder:
  def apply() = new ViewBuilder(None, None, None, None, None, None, None, None)
