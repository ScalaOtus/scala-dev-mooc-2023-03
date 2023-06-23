package collections

import scala.util.Random
class BallsExperiment {
  // 1 - белый шарик, 0 - черный шарик
  def isWhite(): Boolean = Random.nextInt(6) % 2  == 1
}

object BallsTest extends App {
  val count = 10
  val listOfExperiments: List[BallsExperiment] = List.fill(count)(new BallsExperiment)
  val countOfExperiments = listOfExperiments.map(x => x.isWhite() || x.isWhite())
  val countOfPositiveExperiments: Float = countOfExperiments.count(_ == true)
  println(countOfPositiveExperiments / count)
}
