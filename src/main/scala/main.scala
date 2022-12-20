package net.ivoah.cribbagesolitaire

import scala.collection.mutable
import org.scalajs.dom.*
import org.scalajs.dom.html.Canvas

val canvas = document.createElement("canvas").asInstanceOf[Canvas]
implicit val ctx: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

var table: CardTable = null
val undoStack = mutable.Stack[CardTable]()

def repaint(): Unit = {
  table.draw()
}

@main
def main(): Unit = {

  table = CardTable.newGame(newTable => {
    undoStack.push(table)
    table = newTable
    repaint()
  })

  canvas.addEventListener("click", (e: MouseEvent) => {
    table.onclick(e.pageX.toInt, e.pageY.toInt)
  })

  document.addEventListener("DOMContentLoaded", e => {

    canvas.width = window.innerWidth.toInt
    canvas.height = window.innerHeight.toInt
    document.body.appendChild(canvas)

    scala.scalajs.js.timers.setTimeout(100) {
      repaint()
    }
  })

  window.addEventListener("resize", e => {
    canvas.width = window.innerWidth.toInt
    canvas.height = window.innerHeight.toInt
    repaint()
  })
}
