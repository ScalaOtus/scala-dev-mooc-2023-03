package monad

object monad_law {
  //Monad law:

  def decrementF(x: Int): Wrap[Int] = NonEmptyWrap(x - 1)
  def doubleIncrementF(x: Int): Wrap[Int] = NonEmptyWrap(x + 2)

  /**
   * Left unit law:
   * (unit(x) flatMap f) == f(x)
   * */
  def leftUnitLaw(): Unit = {
    assert(NonEmptyWrap(1).flatMap(decrementF) == decrementF(1))
    println("leftUnitLaw check success")
  }

  leftUnitLaw()

  /**
   * Right unit law:
   * (monad flatMap unit) == monad
   * */
  def rightUnitLaw(): Unit = {
    val monad: Wrap[Int] = NonEmptyWrap(10)
    assert(monad.flatMap(NonEmptyWrap(_)) == monad)
    println("rightUnitLaw check success")
  }

  rightUnitLaw()

  /**
   * Associativity law:
   * ((monad flatMap f) flatMap g) == (monad flatMap (x => f(x) flatMap g))
   * */
  def associativityLaw(): Unit = {
    val monad: Wrap[Int] = NonEmptyWrap(2)
    val left = monad.flatMap(decrementF).flatMap(doubleIncrementF)
    val right = monad.flatMap(x => decrementF(x).flatMap(doubleIncrementF))
    assert(left == right)
    println("associativityLaw check success")
  }

  def main(args: Array[String]): Unit = {
    leftUnitLaw()
    rightUnitLaw()
    associativityLaw()
  }

}