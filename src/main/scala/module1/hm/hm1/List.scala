package module1.hm.hm1

import scala.annotation.tailrec

sealed trait List[+T] {

  /**
    * Метод cons, добавляет элемент в голову списка, для этого метода можно воспользоваться названием `::`
    */
  def ::[TT >: T](elem: TT): List[TT] = List.::(elem, this)

  /**
    * Реализовать метод reverse который позволит заменить порядок элементов в списке на противоположный
    */
  final def reverse: List[T] = {
    @tailrec
    def reverseLoop(tailToReverse: List[T], reversed: List[T] = List.Nil): List[T] =
      tailToReverse match {
        case List.Nil            => reversed
        case List.::(head, tail) => reverseLoop(tail, head :: reversed)
      }
    reverseLoop(this)
  }

  /**
    * Реализовать метод map для списка который будет применять некую ф-цию к элементам данного списка
    */
  final def map[B](f: T => B): List[B] = {
    @tailrec
    def mapLoop(initial: List[T], converted: List[B] = List.Nil): List[B] =
      initial match {
        case List.Nil            => converted
        case List.::(head, tail) => mapLoop(tail, f(head) :: converted)
      }
    mapLoop(this).reverse
  }

  /**
    * Метод mkString возвращает строковое представление списка, с учетом переданного разделителя
    */
  def mkString(sep: String): String = {
    @tailrec
    def mkStringLoop(list: List[T], strAcc: String = ""): String =
      list match {
        case List.Nil                => strAcc
        case List.::(head, List.Nil) => strAcc + head
        case List.::(head, tail)     => mkStringLoop(tail, strAcc + head + sep)
      }
    mkStringLoop(this)
  }

  /**
    * Реализовать метод filter для списка который будет фильтровать список по некому условию
    */
  def filter[TT >: T](f: T => Boolean): List[T] = {
    @tailrec
    def filterLoop(initial: List[T], filtered: List[T] = List.Nil): List[T] =
      initial match {
        case List.Nil                        => filtered
        case List.::(head, tail) if f(head)  => filterLoop(tail, head :: filtered)
        case List.::(head, tail) if !f(head) => filterLoop(tail, filtered)
      }
    filterLoop(this).reverse
  }

}

object List {

  case class ::[A](head: A, tail: List[A]) extends List[A]
  case object Nil                          extends List[Nothing]

  def apply[A](v: A*): List[A] = if (v.isEmpty) List.Nil else ::(v.head, apply(v.tail: _*))

  /**
    * Написать функцию incList котрая будет принимать список Int и возвращать список,
    * где каждый элемент будет увеличен на 1
    */
  def incList(list: List[Int]): List[Int] = list.map(_ + 1)

  /**
    * Написать функцию shoutString котрая будет принимать список String и возвращать список,
    * где к каждому элементу будет добавлен префикс в виде '!'
    */
  def shoutString(list: List[String]): List[String] = list.map(_ + "!")
}
