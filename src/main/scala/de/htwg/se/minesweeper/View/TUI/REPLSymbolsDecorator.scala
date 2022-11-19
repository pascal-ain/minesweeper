package de.htwg.se.minesweeper.View.TUI

import de.htwg.se.minesweeper.Util.GameStringDecorator
import de.htwg.se.minesweeper.Controller.Controller
import scala.util.Try
import scala.util.Failure
import scala.util.Success

final case class REPLSymbolsDecorator(
    override val controller: Controller,
    mine: String,
    flag: String,
    closed: String,
    mineCount: Int => String
) extends GameStringDecorator(controller):
  override def toString(): String =
    val eolArr = sys.props("line.separator").toCharArray()
    controller
      .toString()
      .map[String](char =>
        char match
          case 'F'  => flag
          case 'B'  => mine
          case '?'  => closed
          case '\n' => sys.props("line.separator")
          case _    => mineCount(char.asDigit)
      )
      .mkString
