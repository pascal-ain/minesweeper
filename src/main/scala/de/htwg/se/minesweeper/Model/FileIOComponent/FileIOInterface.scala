package de.htwg.se.minesweeper.Model.FileIOComponent

import java.io.File
import de.htwg.se.minesweeper.Model.GameComponent.GameInterface
import scala.util.Try

trait FileIOInterface {
  def load(path: File): Try[GameInterface]
  def save(game: GameInterface): Unit
}
