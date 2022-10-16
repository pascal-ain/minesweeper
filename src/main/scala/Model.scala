private case class Position(
  x: Integer,
  y: Integer,
)

case class Game(
  lost?: Boolean,
  width: Integer,
  heigth: Integer,
  openFields: Set[Position],
  bombs: Set[Position],
  flaggedFields[Position]
)
