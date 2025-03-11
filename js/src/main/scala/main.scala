package net.ivoah.cribbagesolitaire

import scala.collection.mutable
import org.scalajs.dom.*
import org.scalajs.dom.html.Canvas

val canvas = document.createElement("canvas").asInstanceOf[Canvas]
implicit val ctx: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

var table: CardTable = null
val undoStack = mutable.Stack[CardTable]()

var WIDTH = 0
var HEIGHT = 0

def repaint(): Unit = {
  table.draw()
}

@main
def main(): Unit = {
  def makeMove(newTable: CardTable): Unit = {
    undoStack.push(table)
    table = newTable
    repaint()
  }
  table = CardTable.newGame(makeMove)

  val dpr = window.devicePixelRatio
  canvas.style = "width: 100%; height: 100%;"

  canvas.addEventListener("click", (e: MouseEvent) => {
    table.onclick(e.pageX.toInt, e.pageY.toInt)
  })

  document.addEventListener("keyup", (e: KeyboardEvent) => {
    e.key match {
      case "u" if undoStack.nonEmpty =>
        table = undoStack.pop()
        repaint()
      case "n" =>
        makeMove(CardTable.newGame(makeMove))
      case _ =>
    }
  })

  document.addEventListener("DOMContentLoaded", e => {
    WIDTH = window.innerWidth.toInt
    HEIGHT = window.innerHeight.toInt
    canvas.width = (WIDTH * dpr).toInt
    canvas.height = (HEIGHT * dpr).toInt
    ctx.scale(dpr, dpr)
    document.body.appendChild(canvas)

    scala.scalajs.js.timers.setTimeout(100) {
      repaint()
    }
  })

  window.addEventListener("resize", e => {
    WIDTH = window.innerWidth.toInt
    HEIGHT = window.innerHeight.toInt
    canvas.width = (WIDTH * dpr).toInt
    canvas.height = (HEIGHT * dpr).toInt
    ctx.scale(dpr, dpr)

    repaint()
  })
}
