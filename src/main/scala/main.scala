package net.ivoah.cribbagesolitaire

import scala.swing.*

@main
def main(): Unit = {
  var table = CardTable()
  val root = new MainFrame {
    menuBar = new MenuBar {
      contents ++= Seq(
        new MenuItem(Action("New game") {
          println("ne wgame")
        })
      )
    }
    contents = table
  }

  root.centerOnScreen()
  root.open()
}
