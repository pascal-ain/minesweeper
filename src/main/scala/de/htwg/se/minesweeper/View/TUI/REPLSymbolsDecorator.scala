package de.htwg.se.minesweeper.View.TUI

import de.htwg.se.minesweeper.Util.GameStringDecorator
import de.htwg.se.minesweeper.Controller.Controller
import scala.util.Try
import scala.util.Failure
import scala.util.Success

case class REPLSymbolsDecorator(
    controller: Controller,
    mine: String,
    flag: String,
    closed: String,
    mineCount: Int => String
) extends GameStringDecorator(controller):
  override def toString(): String =
    overrideString(super.toString())

  def overrideString(str: String) =
    str
      .map[String](char =>
        char match
          case 'F'  => flag
          case 'B'  => mine
          case '?'  => closed
          case '\n' => sys.props("line.separator")
          case _    => mineCount(char.asDigit)
      )
      .mkString
