package homework.hw2

import scala.util.Random

object Task extends App {

  sealed trait Ball

  case class WhiteBall() extends Ball
  case class BlackBall() extends Ball

  case class Experiment(count: Int) {

    private val f: Int => Ball = {
      case 0 => WhiteBall()
      case 1 => BlackBall()
    }

    private val balls = (1 to count).map(_ => f(Random.nextInt(2)))

    val ff: Ball => Boolean = {
      case WhiteBall() => true
      case BlackBall() => false
    }

    def run: scala.Seq[Boolean] = balls.map(ff)

  }

  val length = 10000000
  val result: Double = Experiment(length).run.count(it => it) / length.toDouble



}