package net.ivoah.cribbagesolitaire

import scala.collection.mutable
import org.scalajs.dom.*
import org.scalajs.dom.html.Canvas

@main
def main(): Unit = {
  val canvas = document.createElement("canvas").asInstanceOf[Canvas]
  implicit val ctx: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  var table: CardTable = null
  val undoStack = mutable.Stack[CardTable]()

  table = CardTable.newGame(newTable => {
    undoStack.push(table)
    table = newTable
    table.draw()
  })

  canvas.addEventListener("click", (e: MouseEvent) => {
    table.onclick(e.pageX.toInt, e.pageY.toInt)
  })

  document.addEventListener("DOMContentLoaded", e => {

    canvas.width = window.innerWidth.toInt
    canvas.height = window.innerHeight.toInt
    document.body.appendChild(canvas)

    scala.scalajs.js.timers.setTimeout(100) {
      table.draw()
    }
  })

  window.addEventListener("resize", e => {
    canvas.width = window.innerWidth.toInt
    canvas.height = window.innerHeight.toInt
    table.draw()
  })
}
