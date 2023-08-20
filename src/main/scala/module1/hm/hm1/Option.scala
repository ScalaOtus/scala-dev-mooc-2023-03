package module1.hm.hm1

import scala.language.postfixOps

sealed trait Option[+T] {

  def isEmpty: Boolean = this match {
    case Option.Some(_) => false
    case Option.None    => true
  }

  def map[B](f: T => B): Option[B] = flatMap(v => Option.Some(f(v)))

  def flatMap[B](f: T => Option[B]): Option[B] = this match {
    case Option.Some(v) => f(v)
    case Option.None    => Option.None
  }

  /**
    * Реализовать метод printIfAny, который будет печатать значение, если оно есть
    */
  def printIfAny(): Unit = if (!isEmpty) map(println)

  /**
    * Реализовать метод zip, который будет создавать Option от пары значений из 2-х Option
    *
    * @example None zip None => None
    *
    *          Some zip None => Some(Some, None)
    *
    *          None zip Some => Some(None, Some)
    */
  def zip[TT](that: Option[TT]): Option[(Option[T], Option[TT])] =
    if (!this.isEmpty || !that.isEmpty) Option.Some(this, that) else Option.None

  /**
    * Реализовать метод filter, который будет возвращать не пустой Option
    * в случае если исходный не пуст и предикат от значения = true
    */
  def filter(f: T => Boolean): Option[T] = if (!isEmpty && map(f) == Option.Some(true)) this else Option.None

}

object Option {

  case class Some[T](v: T) extends Option[T]
  case object None         extends Option[Nothing]

}
