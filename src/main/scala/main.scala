package net.ivoah.cribbagesolitaire

import scala.swing.*

@main
def main(): Unit = {
  val root = new MainFrame {
    private val thisFrame = this
    menuBar = new MenuBar {
      contents ++= Seq(
        new MenuItem(Action("New game") {
          thisFrame.contents = CardTable()
        })
      )
    }
    contents = CardTable()
  }

  root.centerOnScreen()
  root.open()
}
