package de.htwg.se.minesweeper.Util

trait Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(sub: Observer) = subscribers = subscribers :+ sub
  def remove(sub: Observer) = subscribers = subscribers.filterNot(_ == sub)
  def notifyObservers(e: Event) = subscribers.foreach(_.update(e))
