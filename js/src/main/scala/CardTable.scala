package net.ivoah.cribbagesolitaire

import org.scalajs.dom.CanvasRenderingContext2D

import scala.util.Random

object CardTable {
  def newGame(makeMove: CardTable => Unit): CardTable = CardTable(
    Random.shuffle(Card.fullDeck).grouped(52/4).map(CardStack.apply).toSeq,
    CardStack(),
    0,
    makeMove
  )
}

case class CardTable(tableau: Seq[CardStack], stack: CardStack, score: Int, makeMove: CardTable => Unit) {
  def draw()(implicit ctx: CanvasRenderingContext2D): Unit = {
    ctx.save()

    val textHeight = 10

    ctx.fillStyle = "rgb(0, 75, 0)"
    ctx.fillRect(0, 0, WIDTH, HEIGHT)

    for ((s, i) <- tableau.zipWithIndex) {
      s.draw(
        WIDTH - Card.size._1*4 + Card.size._1*i,
        0,
        if (s.usable(stack)) Dim.Tail else Dim.All
      )
    }

    ctx.fillStyle = "white"
    ctx.fillText(s"Score: $score/61", 50, 25)
    ctx.fillText(s"Total: ${stack.value}", 50, 40)
    if (tableau.exists(_.usable(stack))) {
      stack.draw(50, 50, Dim.None)
    } else {
      stack.draw(50, 50, Dim.All)
      ctx.fillText("New stack", 50, 60 + stack.height)
    }
    for (((k, v), i) <- stack.score.zipWithIndex) {
      ctx.fillText(
        s"$k: $v",
        10,
        HEIGHT - textHeight*stack.score.size - 20 + textHeight*i
      )
    }

    ctx.restore()
  }

  def onclick(x: Int, y: Int)(implicit ctx: CanvasRenderingContext2D): Unit = {
    if (!tableau.exists(_.usable(stack)) && stack.clicked(50, 50)(x, y)) {
      makeMove(CardTable(tableau, CardStack(), score, makeMove))
    }

    tableau.zipWithIndex.find { case (s, i) =>
      s.topClicked(WIDTH - Card.size._1 * 4 + Card.size._1*i, 0)(x, y)
      && s.usable(stack)
    }.foreach { case (s, i) =>
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

//  layout(newStackButton) = BorderPanel.Position.South

//  listenTo(mouse.clicks)
}
