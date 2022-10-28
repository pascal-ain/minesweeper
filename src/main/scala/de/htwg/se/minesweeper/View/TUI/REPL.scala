package de.htwg.se.minesweeper.View.TUI
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.*
import scala.io.StdIn.readLine

def repl(game: Game): Any =
  print(">> ")
  parse(readLine(), game) match
    case Some(x) => {
      print(x)
      repl(x)
    }
    case None => {
      print(game.toString() + "invalid command")
      repl(game)
    }