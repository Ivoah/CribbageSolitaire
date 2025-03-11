package net.ivoah.cribbagesolitaire

import org.scalajs.dom.CanvasRenderingContext2D

import scala.annotation.targetName

extension(n: Int) {
  @targetName("!")
  def ! : Int = {
    if (n == 0) 1 else n*(n - 1).!
  }
}

extension(ctx: CanvasRenderingContext2D) {
  def roundRect(x: Int, y: Int, width: Int, height: Int, radius: Int): Unit = {
    ctx.moveTo(x + radius, y)
    ctx.lineTo(x + width - radius, y)
    ctx.quadraticCurveTo(x + width, y, x + width, y + radius)
    ctx.lineTo(x + width, y + height - radius)
    ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height)
    ctx.lineTo(x + radius, y + height)
    ctx.quadraticCurveTo(x, y + height, x, y + height - radius)
    ctx.lineTo(x, y + radius)
    ctx.quadraticCurveTo(x, y, x + radius, y)
  }
}
