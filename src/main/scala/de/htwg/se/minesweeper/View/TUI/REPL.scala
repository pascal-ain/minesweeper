package de.htwg.se.minesweeper.View.TUI
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.*
import scala.io.StdIn.readLine

def repl(game: Game): Unit =
  game.state match
    case State.Lost => println("You lost!")
    case State.Won  => println("You won!")
    case State.OnGoing =>
      print(">> ")
      parse(readLine(), game) match
        case Some(x) => {
          print(x)
          repl(x)
        }
        case None => {
          println(game.toString() + "invalid command")
          repl(game)
        }
