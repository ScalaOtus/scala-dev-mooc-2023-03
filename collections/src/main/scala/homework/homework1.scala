package homework

import scala.util.Random

class BallsExperiment {

  val urna: List[Int] = List(1,1,1,0,0,0)

  def isFirstBlackSecondWhite(): Boolean = {
    val random = new Random()

    val first =random.nextInt(urna.length)

    val ball1 = urna(first)

    val deleteBall = urna.updated(first,-1)

    val second = random.nextInt(deleteBall.length)

    val ball2 = deleteBall(second)

    if (ball1 == 1 || ball2 == 1) true
    else false
  }
}

object BallsTest {
  def main(args: Array[String]): Unit = {
    val count = 10000
    val listOfExperiments: List[BallsExperiment] = List.fill(count)(new BallsExperiment)
    val countOfExperiments = listOfExperiments.map(_.isFirstBlackSecondWhite())
    val countOfPositiveExperiments: Float = countOfExperiments.count(_ == true)
    println(countOfPositiveExperiments / count)
  }
}