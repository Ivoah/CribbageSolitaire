package net.ivoah.cribbagesolitaire

import java.awt.geom.RoundRectangle2D
import java.awt.{BasicStroke, Color}
import javax.imageio.ImageIO
import scala.swing.*

object Suite extends Enumeration {
  val Spades, Hearts, Clubs, Diamonds = Value
}

case class Card(value: Int, suite: Suite.Value) {
  private val img: Image = ImageIO.read(getClass.getResource(s"/cards/front-${suite.id*13 + (value - 1)}.png"))
  private val name = Card.names(value - 1)
  override def toString: String = s"$name of $suite"

  val scoreValue: Int = if (value >= 10) 10 else value

  def draw(x: Int, y: Int, dim: Boolean = false)(implicit g: Graphics2D): Unit = {
    val oldClip = g.getClip
    val oldStroke = g.getStroke
    val oldPaint = g.getPaint

    g.setPaint(Color.BLACK)
    g.setStroke(BasicStroke(1))

    val border = RoundRectangle2D.Double(x, y, Card.size.width, Card.size.height, 10, 10)
    g.setClip(border)
    g.drawImage(img, x, y, Card.size.width, Card.size.height, null)
    if (dim) {
      g.setPaint(Color(0, 0, 0, 25))
      g.fill(border)
    }
    g.draw(border)

    g.setClip(oldClip)
    g.setStroke(oldStroke)
    g.setPaint(oldPaint)
  }

  def drawBack(x: Int, y: Int)(implicit g: Graphics2D): Unit = {
    val oldClip = g.getClip
    val oldStroke = g.getStroke
    val oldPaint = g.getPaint

    g.setPaint(Color.BLACK)
    g.setStroke(BasicStroke(1))

    val border = RoundRectangle2D.Double(x, y, Card.size.width, Card.size.height, 10, 10)
    g.setClip(border)
    g.drawImage(Card.cardBack, x, y, Card.size.width, Card.size.height, null)
    g.draw(border)

    g.setClip(oldClip)
    g.setStroke(oldStroke)
    g.setPaint(oldPaint)
  }
}

object Card {
  val size: Dimension = new Dimension(80, 112)
  private val names = Seq(
    "Ace", "Two", "Three", "Four",
    "Five", "Six", "Seven", "Eight",
    "Nine", "Ten", "Jack", "Queen", "King"
  )

  val fullDeck: Seq[Card] = Suite.values.toSeq.flatMap(s => names.map(v => Card(v, s)))
  private val cardBack: Image = ImageIO.read(getClass.getResource("/cards/back.png"))

  def apply(value: String, suite: Suite.Value): Card = Card(names.indexOf(value) + 1, suite)
}

case class Stack(cards: Seq[Card]) {
  val spacing = 30

  def draw(x: Int, y: Int)(implicit g: Graphics2D): Unit = {
    for ((card, i) <- cards.zipWithIndex) {
      card.draw(x, y + i*spacing, i < cards.length - 1)
    }
  }
}