package net.ivoah.cribbagesolitaire

import java.awt.geom.*
import java.awt.{BasicStroke, Color}
import javax.imageio.ImageIO
import scala.annotation.targetName
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

object Dim extends Enumeration {
  val All, None, Tail = Value
}

case class CardStack(cards: Seq[Card] = Seq()) {
  private val spacing = 25

  def draw(x: Int, y: Int, dim: Dim.Value = Dim.Tail)(implicit g: Graphics2D): Unit = {
    for ((card, i) <- cards.reverse.zipWithIndex) {
      card.draw(x, y + i*spacing, dim == Dim.All || (dim == Dim.Tail && i < cards.size - 1))
    }
  }

  def score: Map[String, Int] = Map(
    "First card played to the stack is a Jack" -> {
      if (cards.size == 1 && cards.head.value == 11) 2 else 0
    },
    "Stack total is exactly 15" -> {
      if (value == 15) 2 else 0
    },
    "Stack total is exactly 31" -> {
      if (value == 31) 2 else 0
    },
    "Set of 2, 3, or 4 of the same card" -> {
      (2 to 4).findLast(n => cards.size >= n && cards.take(n).forall(c => c.value == cards.head.value)).map(n => n.! / (n - 2).!).getOrElse(0)
    },
    "Run of 3 to 7 cards, in any order" -> {
      (3 to 7).findLast(n => cards.size >= n && cards.take(n).map(_.value).sorted.sliding(2).forall{case Seq(c1, c2) => c1 + 1 == c2}).getOrElse(0)
    }
  ).filter(_._2 > 0)

  def value: Int = cards.map(_.scoreValue).sum
  def usable(stack: CardStack): Boolean = cards.headOption.exists(_.scoreValue + stack.value <= 31)

  def boundingBox(x: Int, y: Int): Rectangle = new Rectangle(
    x,
    y,
    Card.size.width,
    if (cards.nonEmpty) Card.size.height + spacing*(cards.size - 1) else 0
  )

  def top: Card = cards.head
  def tail: CardStack = CardStack(cards.tail)

  @targetName(":+")
  def :+(card: Card): CardStack = CardStack(card +: cards)
}
