package m5

import scala.util.Random

class BallsExperiment {
  /**
   * Максимальное количество шаров черного цвета, которые могут быть в урне
   */
  val MaxBlackBallsInUrn = 3;

  /**
   * Максимальное количество шаров белого цвета, которые могут быть в урне
   */
  val MaxWhiteBallsInUrn = 3;

  /**
   * Шары в урне лежат по порядку
   */
  val orderedUrn = List(List.fill(MaxBlackBallsInUrn) {false}, List.fill(MaxWhiteBallsInUrn) {true})
    .flatMap(ball => ball)

  /**
   * Мешаем шары в урне столько раз, сколько их всего
   */
  val shakedUrn = shakeRec(orderedUrn, MaxBlackBallsInUrn+MaxWhiteBallsInUrn)

  /**
   * Убираем шар из урны
   * @param list - шары у урне
   * @param elNum - какой по порядку шар убрать
   * @tparam T
   * @return - вытащенный шар, оставшиеся шары
   */
  def remove[T](list: List[T], elNum:Int):(T,List[T]) = {
    (
      list(elNum), list.take(elNum) ++ list.drop(elNum + 1)
    )
  }

  /**
   * Мешаем шары в урне
   * @param list - шары в урне
   * @param n - количество перемешиваний
   * @tparam T
   * @return - урна с перемешанными шарами
   */
  def shakeRec[T](list: List[T], n:Int): List[T] = {
    if(n == 0)
    list
    else {
      val ballNum = Random.nextInt(list.size)
      shakeRec(list(ballNum) :: remove(list, ballNum)._2, n - 1)
    }
  }

  def isFirstBlackSecondWhite(): Boolean = {
    remove(shakedUrn, Random.nextInt(shakedUrn.size)) match {
      // Первый шар чёрный
      case (false, listWithoutOneBlack)=>
        remove(listWithoutOneBlack, Random.nextInt(listWithoutOneBlack.size)) match {
        // Второй шар белый
          case (true, _) => true
        // Второй шар чёрный
          case (false, _) => false
        }
    // Первый шар белый - дальнеёшее не интересует
      case (true, _) => false
    }
  }
}

object BallsTest {
  def main(args: Array[String]): Unit = {
    val count = 10000
    val listOfExperiments: List[BallsExperiment] = List.from(for (i <- 0 to count) yield {
      new BallsExperiment()
    })
    val countOfExperiments: List[Boolean] = listOfExperiments.map {
      experiment =>
        experiment.isFirstBlackSecondWhite()
    }
    val countOfPositiveExperiments: Float = countOfExperiments.count(_ == true)
    println(countOfPositiveExperiments / count)
  }
}


