package de.htwg.se.minesweeper.Model.FileIOComponent

import java.io.File
import de.htwg.se.minesweeper.Model.GameComponent.GameInterface

trait FileIOInterface:
  def load(path: File): GameInterface
  def save(game: GameInterface): Unit
