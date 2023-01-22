package de.htwg.se.minesweeper.Model.FileIOComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Model.GameComponent.*
import scala.util.{Left => Err, Right => Ok}

class FileIOInterfaceSpec extends AnyWordSpec {
  "FileIO" should {
    val fileIO = FileIOTOMLImplementation.FileIO
    val height = 9
    val width = 9
    val mineOutOfBounds = Set(Position(9, 9), Position(0, 0))
    val flagOutOfBounds = Set(Position(9, 10), Position(1, 2))
    val openOutOfBounds = Set(Position(11, 2), Position(2, 5))

    val flagIntersectOpen = Set(Position(0, 0), Position(4, 4))
    val openIntersectFlag = Set(Position(3, 2), Position(4, 4))
    "reject invalid data" in {
      fileIO
        .verifyData(
          width,
          height,
          openOutOfBounds,
          Set.empty,
          Set.empty
        )
        .toOption shouldBe None
      fileIO
        .verifyData(width, height, Set.empty, flagOutOfBounds, Set.empty)
        .toOption shouldBe None
      fileIO
        .verifyData(width, height, Set.empty, Set.empty, mineOutOfBounds)
        .toOption shouldBe None
      fileIO
        .verifyData(
          width,
          height,
          openIntersectFlag,
          flagIntersectOpen,
          Set.empty
        )
        .toOption shouldBe None
    }
  }
}
