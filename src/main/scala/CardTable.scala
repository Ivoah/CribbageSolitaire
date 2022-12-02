package net.ivoah.cribbagesolitaire

import java.awt.geom.RoundRectangle2D
import java.awt.{BasicStroke, Color}
import javax.swing.Timer
import scala.swing.*
import scala.swing.event.*
import scala.util.Random

object CardTable {
  def newGame(makeMove: CardTable => Unit): CardTable = CardTable(
    Random.shuffle(Card.fullDeck).grouped(52/4).map(CardStack.apply).toSeq,
    CardStack(),
    0,
    makeMove
  )
}

case class CardTable(tableau: Seq[CardStack], stack: CardStack, score: Int, makeMove: CardTable => Unit) extends BorderPanel {
  focusable = true
  preferredSize = new Dimension(640, 480)

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
    makeMove(CardTable(tableau, CardStack(), score, makeMove))
  }) {
    enabled = !tableau.exists(_.usable(stack))
  }

  mouse.clicks.reactions += {
    case MouseReleased(source, point, modifiers, clicks, triggersPopup) =>
      tableau.zipWithIndex.find { case (s, i) =>
        s.boundingBox(size.width - Card.size.width * 4 + Card.size.width*i, 0).contains(point)
        && s.usable(stack)
      }.map { case (s, i) =>
        makeMove(CardTable(
          tableau.zipWithIndex.map {
            case (s, `i`) => s.tail
            case (s, _) => s
          },
          stack :+ s.top,
          score + (stack :+ s.top).score.values.sum,
          makeMove
        ))
      }
  }

  layout(newStackButton) = BorderPanel.Position.South

  listenTo(mouse.clicks)
}
