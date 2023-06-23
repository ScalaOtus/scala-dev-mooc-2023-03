package DataCollection2


object MonadRules{
  def squareFunction(x: Int): Option[Int] = Some(x*x)
  def incrementFunction(x: Int): Option[Int] = Some(x+1)

  //1. leftUnitLaw
  // monad.apply(x) flatMap f == f(x)
  def leftUnitLaw(): Unit = {
    val x = 5
    val monad: Option[Int] = Some(x)
    val result = monad.flatMap(squareFunction) == squareFunction(x)
    println(result)
  }

  //2. rightUnitLow
  // monad(x) flatMap monad.apply == monad
  def rightUnitLow(): Unit = {
    val x = 5
    val monad: Option[Int] = Some(x)

    val result = monad.flatMap(x => Some(x)) == monad
    println(result)
  }

  def associativeLaw(): Unit ={
    val x = 5
    val monad: Option[Int] = Some(x)

    val left = monad flatMap squareFunction flatMap incrementFunction
    val right = monad flatMap(x=> squareFunction(x) flatMap incrementFunction)
    assert(left ==right)
  }

  def main(arg: Array[String]) : Unit = {

    leftUnitLaw
    rightUnitLow
    associativeLaw

  }



}