package net.ivoah.cribbagesolitaire

import java.awt.{BasicStroke, Color}
import java.awt.geom.RoundRectangle2D
import scala.swing.*
import scala.util.Random
import scala.collection.mutable
import javax.swing.Timer
import scala.swing.event.*

class CardTable extends BorderPanel {
  focusable = true
  preferredSize = new Dimension(640, 480)

  private val tableau = Random.shuffle(Card.fullDeck).grouped(52/4).map(s => CardStack(s*)).toSeq
  private val stack = CardStack()
  private var score = 0

  override def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    implicit val implicitGraphics: Graphics2D = g

    g.setPaint(Color(0, 75, 0))
    g.fillRect(0, 0, size.width, size.height)

    for ((s, i) <- tableau.zipWithIndex) {
      s.draw(
        size.width - Card.size.width*4 + Card.size.width*i,
        0,
        if (s.usable(stack)) Dim.Tail else Dim.All
      )
    }

    g.setPaint(Color.WHITE)
    g.drawString(s"Score: ${score}", 50, 25)
    g.drawString(s"Total: ${stack.value}", 50, 40)
    stack.draw(50, 50, Dim.None)
    for (((k, v), i) <- stack.score.zipWithIndex) {
      g.drawString(
        s"$k: $v",
        10,
        size.height - g.getFontMetrics.getHeight*stack.score.size - 20 + g.getFontMetrics.getHeight*i
      )
    }

    g.setPaint(Color.BLACK)
    g.fillOval(size.width - 10, size.height - 10, 5, 5)
  }

  private val newStackButton: Button = new Button(Action("New stack") {
    stack.clear()
    newStackButton.peer.setEnabled(false)
    repaint()
  }) {
    enabled = false
  }

  mouse.clicks.reactions += {
    case MouseReleased(source, point, modifiers, clicks, triggersPopup) =>
      tableau.zipWithIndex.find { case (s, i) =>
        s.boundingBox(size.width - Card.size.width * 4 + Card.size.width*i, 0).contains(point)
        && s.usable(stack)
      }.map { case (s, i) =>
        stack.push(s.pop())
        score += stack.score.values.sum
        if (!tableau.exists(_.usable(stack))) {
          newStackButton.enabled = true
        }
        repaint()
      }
//    case e => println(e)
  }

  layout(newStackButton) = BorderPanel.Position.South

  listenTo(mouse.clicks)
}
