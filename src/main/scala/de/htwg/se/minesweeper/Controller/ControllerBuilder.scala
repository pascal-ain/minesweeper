package de.htwg.se.minesweeper.Controller

import scala.util.{Try, Failure, Success}
import scala.util.{Either, Right => Ok, Left => Err}
import de.htwg.se.minesweeper.Model.Game
import de.htwg.se.minesweeper.View.TUI.REPL
import de.htwg.se.minesweeper.View.GUI.GUI
import de.htwg.se.minesweeper.Controller.Controller

trait GameBuilder:
  def width(x: Int): GameBuilder
  def height(y: Int): GameBuilder
  def mines(percentage: Double): GameBuilder
  def build: Try[Controller]

object ControllerBuilder:
  def apply() =
    new ControllerBuilder(
      Err("width"),
      Err("height"),
      Err("mine percentage")
    )

case class ControllerBuilder(
    height: Either[String, Int],
    width: Either[String, Int],
    mines: Either[String, Double]
) extends GameBuilder:
  override def width(x: Int) = copy(width = Ok(x + 1))
  override def height(y: Int) = copy(height = Ok(y + 1))
  override def mines(num: Double) =
    require(num < 1)
    copy(mines = Ok(num))

  override def build: Try[Controller] =
    val properties = (
      width,
      height,
      mines
    )
    properties match
      case (
            Ok(width),
            Ok(height),
            Ok(mines)
          ) =>
        Success(new Controller(Game(width, height, mines)))
      case _ => reportError(properties.toIArray)

  def reportError(fields: Iterable[Any]): Try[Controller] =
    Failure(
      IllegalArgumentException(
        fields
          .asInstanceOf[Iterable[Either[String, _]]]
          .filter(_.isLeft)
          .map(_.left.toOption.get)
          .mkString(", ") + " missing!"
      )
    )
