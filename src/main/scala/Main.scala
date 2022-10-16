import Model.*

val eol = sys.props("line.separator")

@main def run(difficulty: String*): Unit =
  val msg = "start the program with <easy> for 9x9 board" + eol + "<medium> for 16x16 board" + eol + "<hard> for 30x16 board" + eol
  if difficulty.isEmpty then
    print(msg)
  else
    difficulty.apply(0) match
    case "easy" => print(Game())
    case "medium" => print(Game(16, 16))
    case "hard" => print(Game(30, 16))
    case _ => print(msg)

