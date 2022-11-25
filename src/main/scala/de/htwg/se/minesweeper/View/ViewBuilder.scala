package de.htwg.se.minesweeper.View

import de.htwg.se.minesweeper.Util.{MyApp, AppBuilder, ViewType}
import scala.util.{Try, Failure, Success}
import scala.util.{Either, Right => Ok, Left => Err}
import de.htwg.se.minesweeper.Model.Game
import de.htwg.se.minesweeper.View.TUI.REPL
import de.htwg.se.minesweeper.View.GUI.GUI

object ViewBuilder:
  def apply() = new ViewBuilder(
    Err("interface type"),
    Err("width"),
    Err("height"),
    Err("mine percentage"),
    Err("flag symbol"),
    Err("mine symbol"),
    Err("closed field symbol"),
    Err("score symbols")
  )

case class ViewBuilder(
    interface: Either[String, ViewType],
    width: Either[String, Int],
    height: Either[String, Int],
    mines: Either[String, Double],
    flagSymbol: Either[String, String],
    mineSymbol: Either[String, String],
    closedFieldSymbol: Either[String, String],
    scoreSymbols: Either[String, Int => String]
) extends AppBuilder:
  override def viewType(kind: ViewType) = copy(interface = Ok(kind))
  override def width(x: Int) = copy(width = Ok(x + 1))
  override def height(y: Int) = copy(height = Ok(y + 1))
  override def mines(num: Double) =
    require(num < 1)
    copy(mines = Ok(num))

  override def flagSymbol(flag: String) = copy(flagSymbol = Ok(flag))
  override def mineSymbol(mine: String) = copy(mineSymbol = Ok(mine))
  override def closedFieldSymbol(closed: String) =
    copy(closedFieldSymbol = Ok(closed))
  override def scoreSymbols(conv: Int => String) =
    copy(scoreSymbols = Ok(conv))
  override def build: Try[MyApp] =
    val properties = (
      interface,
      width,
      height,
      mines,
      mineSymbol,
      flagSymbol,
      closedFieldSymbol,
      scoreSymbols
    )
    properties match
      case (
            Ok(interface),
            Ok(width),
            Ok(height),
            Ok(mines),
            Ok(mineSymbol),
            Ok(flagSymbol),
            Ok(closedFieldSymbol),
            Ok(scoreSymbols)
          ) =>
        interface match
          case ViewType.TUI =>
            Success(
              new REPL(
                Game(width, height, mines),
                mineSymbol,
                flagSymbol,
                closedFieldSymbol,
                scoreSymbols
              )
            )
          case ViewType.GUI =>
            Success(
              new GUI(
                Game(width, height, mines),
                mineSymbol,
                flagSymbol,
                closedFieldSymbol,
                scoreSymbols
              )
            )
      case _ => reportError(properties.toIArray)

  def reportError(fields: Iterable[Any]): Try[MyApp] =
    Failure(
      IllegalArgumentException(
        fields
          .asInstanceOf[Iterable[Either[String, _]]]
          .filter(_.isLeft)
          .map(_.left.toOption.get)
          .mkString(", ") + " missing!"
      )
    )
