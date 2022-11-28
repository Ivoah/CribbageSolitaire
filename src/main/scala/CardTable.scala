package net.ivoah.cribbagesolitaire

import java.awt.Color
import scala.swing.*
import scala.util.Random
import scala.collection.mutable
import javax.swing.Timer

class CardTable extends Panel {
  focusable = true
  preferredSize = new Dimension(640, 480)

  private val deck = mutable.Stack[Card](Card.fullDeck: _*)
  private var moving: Option[(Card, Int, Double)] = None
  private val stacks = mutable.Seq(Stack(Seq()), Stack(Seq()), Stack(Seq()), Stack(Seq()))

  private val timer: Timer = new Timer(10, { _ =>
    moving match {
      case Some((card, dest, progress)) =>
        if (progress >= 1) {
          stacks(dest) = Stack(stacks(dest).cards :+ card)
          moving = None
        } else {
          moving = Some((card, dest, progress + 0.25))
        }
      case None =>
        if (deck.nonEmpty) {
          moving = Some(deck.pop(), stacks.zipWithIndex.minBy(_._1.cards.length)._2, 0)
        } else {
          timer.stop()
        }
    }
    repaint()
  })
  timer.start()

  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    implicit val implicitGraphics: Graphics2D = g

    g.setPaint(Color(0, 75, 0))
    g.fillRect(0, 0, size.width, size.height)

    for ((card, i) <- deck.zipWithIndex) {
      card.drawBack(50, size.height - Card.size.height - 50 - i/2)
    }

    for ((stack, i) <- stacks.zipWithIndex) {
      stack.draw(size.width - Card.size.width*4 + Card.size.width*i, 0)
    }

    moving match {
      case Some((card, dest, progress)) =>
        val from = (50, size.height - Card.size.height - 50 - deck.length/2)
        val to = (size.width - Card.size.width*4 + Card.size.width*dest, stacks(dest).cards.length*stacks(dest).spacing)
        card.draw(
          (from._1 + (to._1 - from._1)*progress).toInt,
          (from._2 + (to._2 - from._2)*progress).toInt
        )
      case None =>
    }

    g.setPaint(Color.BLACK)
    g.fillOval(size.width - 10, size.height - 10, 5, 5)
  }
}
