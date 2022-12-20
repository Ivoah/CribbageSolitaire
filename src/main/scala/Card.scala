package net.ivoah.cribbagesolitaire

import scala.annotation.targetName
import org.scalajs.dom.{CanvasRenderingContext2D, HTMLImageElement}

object Suite extends Enumeration {
  val Spades, Hearts, Clubs, Diamonds = Value
}

case class Card(value: Int, suite: Suite.Value) {
  /*private*/ val img: HTMLImageElement = Image(s"target/scala-3.2.1/classes/cards/front-${suite.id * 13 + (value - 1)}.png")
  private val name = Card.names(value - 1)
  override def toString: String = s"$name of $suite"

  val scoreValue: Int = if (value >= 10) 10 else value

  def draw(x: Int, y: Int, dim: Boolean = false)(implicit ctx: CanvasRenderingContext2D): Unit = {
    ctx.save()

    ctx.beginPath()
    ctx.roundRect(x, y, Card.size._1, Card.size._2, 10)
    ctx.closePath()
    ctx.clip()
    ctx.drawImage(Card.imgs((value, suite)), x, y, Card.size._1, Card.size._2)
    if (dim) {
      ctx.fillStyle = "rgba(0, 0, 0, 0.25)"
      ctx.fill()
    }
    ctx.stroke()

    ctx.restore()
  }

  def drawBack(x: Int, y: Int)(implicit ctx: CanvasRenderingContext2D): Unit = {
  ctx.save()

  ctx.beginPath()
  ctx.roundRect(x, y, Card.size._1, Card.size._2, 10)
  ctx.closePath()
  ctx.clip()
  ctx.drawImage(Card.cardBack, x, y, Card.size._1, Card.size._2)
  ctx.stroke()

  ctx.restore()
  }
}

object Card {
  val size: (Int, Int) = (80, 112)
  private val names = Seq(
    "Ace", "Two", "Three", "Four",
    "Five", "Six", "Seven", "Eight",
    "Nine", "Ten", "Jack", "Queen", "King"
  )

  private val imgs = (1 to 13).flatMap(value => Suite.values.unsorted.map(suite => (value, suite) -> Image(s"target/scala-3.2.1/classes/cards/front-${suite.id * 13 + (value - 1)}.png"))).toMap

  val fullDeck: Seq[Card] = Suite.values.toSeq.flatMap(s => names.map(v => Card(v, s)))
  private val cardBack = Image("target/scala-3.2.1/classes/cards/back.png")

  def apply(value: String, suite: Suite.Value): Card = Card(names.indexOf(value) + 1, suite)
}

object Dim extends Enumeration {
  val All, None, Tail = Value
}

case class CardStack(cards: Seq[Card] = Seq()) {
  private val spacing = 25

  def draw(x: Int, y: Int, dim: Dim.Value = Dim.Tail)(implicit ctx: CanvasRenderingContext2D): Unit = {
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

  def clicked(x0: Int, y0: Int)(x1: Int, y1: Int): Boolean = {
    x1 >= x0
      && x1 < x0 + Card.size._1
      && y1 >= y0
      && y1 <= y0 + height
  }

  def topClicked(x0: Int, y0: Int)(x1: Int, y1: Int): Boolean = {
    x1 >= x0
      && x1 < x0 + Card.size._1
      && y1 >= y0 + spacing * (cards.size - 1)
      && y1 <= y0 + spacing * (cards.size - 1) + (if (cards.nonEmpty) Card.size._2 else 0)
  }

  def height: Int = if (cards.nonEmpty) Card.size._2 + spacing*(cards.size - 1) else 0
  def top: Card = cards.head
  def tail: CardStack = CardStack(cards.tail)

  @targetName(":+")
  def :+(card: Card): CardStack = CardStack(card +: cards)
}
