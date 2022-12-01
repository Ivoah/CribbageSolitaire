package net.ivoah.cribbagesolitaire

import java.awt.geom.*
import java.awt.{BasicStroke, Color}
import javax.imageio.ImageIO
import scala.collection.mutable
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

class CardStack(cards: Card*) extends mutable.Stack[Card] {
  this.addAll(cards)
  val spacing = 25

  def draw(x: Int, y: Int, dim: Dim.Value = Dim.Tail)(implicit g: Graphics2D): Unit = {
    for ((card, i) <- this.reversed.zipWithIndex) {
      card.draw(x, y + i*spacing, dim == Dim.All || (dim == Dim.Tail && i < this.size - 1))
    }
  }

  def score: Map[String, Int] = Map(
    "First card played to the stack is a Jack" -> {
      if (this.size == 1 && this.head.value == 11) 2 else 0
    },
    "Stack total is exactly 15" -> {
      if (this.value == 15) 2 else 0
    },
    "Stack total is exactly 31" -> {
      if (this.value == 31) 2 else 0
    },
    "Set of 2, 3, or 4 of the same card" -> {
      (2 to 4).findLast(n => this.size >= n && this.take(n).forall(c => c.value == this.head.value)).map(n => n.! / (n - 2).!).getOrElse(0)
    },
    "Run of 3 to 7 cards, in any order" -> {
      (3 to 7).findLast(n => this.size >= n && this.take(n).map(_.value).sorted.toSeq.sliding(2).forall{case Seq(c1, c2) => c1 + 1 == c2}).getOrElse(0)
    }
  ).filter(_._2 > 0)

  def value: Int = this.map(_.scoreValue).sum
  def usable(stack: CardStack): Boolean = this.headOption.exists(_.scoreValue + stack.value <= 31)

  def boundingBox(x: Int, y: Int): Rectangle = new Rectangle(
    x,
    y,
    Card.size.width,
    if (this.nonEmpty) Card.size.height + spacing*(this.size - 1) else 0
  )
}
