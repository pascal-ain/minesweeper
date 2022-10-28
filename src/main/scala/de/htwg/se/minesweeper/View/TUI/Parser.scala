package de.htwg.se.minesweeper.View.TUI
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.*

//TODO fix parsing

def parse(command: String, game: Game): Option[Game] =
  val token = command.split("\\s+")
  checkToken(token, game)

def checkToken(tokens: Array[String], game: Game): Option[Game] =
  if tokens.length == 1 || tokens.length == 2 then None
  else
    tokens(0).toLowerCase() match
      case "exit" => Some(handleCommand(game, Commands.Exit, Position(0, 0)))
      case "open" | "flag" =>
        if tokens.length == 2 then Some(parsePosition(tokens, game))
        else None
      case _ => Some(handleCommand(game, Commands.Invalid, Position(0, 0)))

def parsePosition(tokens: Array[String], game: Game) =
  if tokens(1).matches("\\d+,\\d+") then
    val coords = tokens(1).split(",")
    val x = coords(0).toInt
    val y = coords(1).toInt
    tokens(0) match
      case "open" => openField(game, Position(x, y))
      case "flag" => flagField(game, Position(x, y))
      case _      => game
  else game
