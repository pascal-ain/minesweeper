package de.htwg.se.minesweeper.View.TUI
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.*

// TODO: maybe don't start counting the x,y from 0?

def parse(command: String, game: Game): Option[Game] =
  checkToken(command.split("\\s+"), game)

def checkToken(tokens: Array[String], game: Game): Option[Game] =
  if tokens.length == 1 && tokens(0).toLowerCase() == "exit" then sys.exit(0)
  else if tokens.length == 2 then parsePosition(tokens, game)
  else None

def parsePosition(tokens: Array[String], game: Game) =
  if tokens(1).matches("\\d+,\\d+") then
    val coords = tokens(1).split(",")
    val x = coords(0).toInt
    val y = coords(1).toInt
    tokens(0) match
      case "open" => Some(openField(game, Position(x, y)))
      case "flag" => Some(flagField(game, Position(x, y)))
      case _      => None
  else None
