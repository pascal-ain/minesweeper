package de.htwg.se.minesweeper.Model.GameComponent

final case class SnapShot(
    openFields: Map[Position, Int | Mine.type],
    flaggedFields: Set[Position],
    mines: Set[Position],
    state: State
)
