package net.ivoah.cribbagesolitaire

import scala.collection.mutable
import scala.swing.*

@main
def main(): Unit = {
  val undoStack = mutable.Stack[CardTable]()

  val root = new MainFrame {
    private val thisFrame = this

    private def makeMove(table: CardTable): Unit = {
      undoStack.push(contents.head.asInstanceOf[CardTable])
      contents = table
      repaint()
    }

    menuBar = new MenuBar {
      contents ++= Seq(
        new MenuItem(Action("New game") {
          makeMove(CardTable.newGame(makeMove))
        }),
        new MenuItem(Action("Undo") {
          if (undoStack.nonEmpty) {
            thisFrame.contents = undoStack.pop()
            thisFrame.repaint()
          }
        })
      )
    }
    contents = CardTable.newGame(makeMove)
  }

  root.centerOnScreen()
  root.open()
}
