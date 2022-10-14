val eol = sys.props("line.separator")
def randomSymbol: String =
  import scala.util.Random
  Random.between(1, 4) match
  case 1 => " □ "
  case 2 => " B "
  case 3 => " F "

def field(width: Int = 9, height: Int = 9): String =
  def recurField(recurHeight: Int, accum: String): String =
    def recurLine(recurWidth: Int, accum: String): String =
      if recurWidth == 0 then accum
      else recurLine(recurWidth - 1, accum + randomSymbol)
    if recurHeight == 0 then accum
    else recurField(recurHeight - 1, accum + recurLine(height, "") + eol)
  recurField(width, "")

@main def run(flag: String*): Unit =
  val msg = "start the program with <easy> for 9x9 board" + eol + "starting with <medium> for 16x16 board" + eol + "<hard> for 30x16 board" + eol
  if flag.isEmpty then
    print(field(9,9))
  else
    flag.apply(0) match
    case "easy" => print(field())
    case "medium" => print(field(16,16))
    case "hard" => print(field(30,16))
    case _ => print(msg)
