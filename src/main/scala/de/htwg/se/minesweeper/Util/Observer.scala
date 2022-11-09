package de.htwg.se.minesweeper.Util

trait Observer:
  def update(e: Event): Unit
