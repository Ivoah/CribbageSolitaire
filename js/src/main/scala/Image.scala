package net.ivoah.cribbagesolitaire

import org.scalajs.dom
import org.scalajs.dom.HTMLImageElement

def Image(src: String): HTMLImageElement = {
  val element: HTMLImageElement = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
  element.onload = _ => repaint()
  element.src = src
  element
}
