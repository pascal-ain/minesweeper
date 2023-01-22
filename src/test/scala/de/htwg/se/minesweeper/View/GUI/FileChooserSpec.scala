package de.htwg.se.minesweeper.View.GUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import java.io.File
import de.htwg.se.minesweeper.Config

class FileChooserSpec extends AnyWordSpec {
  val loadFc = LoadFileChooser
  "Each of the filechoosers" should {
    "only handle XML, TOML and JSON" in {
      val extensions = List("toml", "xml", "json")
      loadFc.filters.map(
        _.getExtensions()(0)
      ) should contain allElementsOf extensions
    }
    "detect which extension a file has (only json, xml or toml)" in {
      def verify(fileName: String, ext: String) =
        val (file, ex) = loadFc.processFile(new File(fileName))
        file.toString shouldBe fileName
        ex shouldBe ext

      verify("hacker.xml", "xml")
      verify("hacking.json", "json")
      verify("1337.toml", "toml")
      // default is toml

      val name = "wikipeda.csv"
      val (fileName, ex) = loadFc.processFile(new File(name))
      println(fileName.toString())
      fileName
        .toString() shouldBe s"${name}.${Config.defaultFileExtension}"
    }
  }
}
