package de.htwg.se.minesweeper.View.GUI

import scala.swing.*
import de.htwg.se.minesweeper.Config
import scala.swing.event.*

class GameSetter() extends Dialog {
  var minePercentage = Config.minePercentage
  var width = Config.defaultWidth
  var height = Config.defaultHeight
  var buttonSizes = 60

  var percentage = new TextField() {
    tooltip = "Desired percentage of mines"
    text = minePercentage.toString().stripPrefix("0.")
  }
  var percentageField = new GridPanel(1, 2) {
    contents ++= Seq(percentage, new Label("%"))
    tooltip = "Desired mine percentage"
  }

  def result = (width, height, minePercentage)
  modal = true

  contents = new GridPanel(2, 1) {
    contents += percentageField
    contents += new GridPanel(1, 3) {
      contents ++= Seq(
        ButtonSetter(10, 10),
        ButtonSetter(16, 16),
        ButtonSetter(30, 16)
      )
    }
  }

  pack()
  centerOnScreen()
  open()

  case class ButtonSetter(setWidth: Int, setHeight: Int) extends Button {
    text = s"${setWidth}x${setHeight}"

    listenTo(mouse.clicks)
    reactions += {
      case MouseClicked(_) => {
        val msg = "Only numbers from 1 to 99 are allowed!"
        val err = () =>
          close()
          Dialog.showMessage(message = msg, title = "Error")
          Config.minePercentage

        width = setWidth
        height = setHeight
        buttonSizes = (width * height) match
          case result if result <= 100               => 60
          case result if result <= 256               => 55
          case result if result <= 480               => 45
          case result if result <= Integer.MAX_VALUE => 30

        minePercentage = percentage.text.toIntOption match
          case None =>
            err()
          case Some(value) => {
            if 0 < value && value < 100 then
              close()
              s"0.$value".toDouble
            else err()
          }
      }
    }
  }
}
