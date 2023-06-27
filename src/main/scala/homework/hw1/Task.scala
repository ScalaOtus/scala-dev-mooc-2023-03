package homework.hw1

import java.util.function.Predicate
import scala.annotation.tailrec

object Task extends App {

  object opt {

    /** Реализовать структуру данных Option, который будет указывать на присутствие либо отсутсвие результата
      */

    // Animal -> Dog
    // Covariant + отношения переносятся на контейнер
    // Contravariant - отношения переносятся на контейнер наоборот
    // Invariant - нет отношений
    type Dog

    sealed trait Option[+T] {
      def isEmpty: Boolean = this match {
        case Option.Some(v) => false
        case Option.None    => true
      }

      def map[B](f: T => B): Option[B] =
        flatMap(v => Option.Some(f(v)))

      def flatMap[B](f: T => Option[B]): Option[B] = this match {
        case Option.Some(v) => f(v)
        case Option.None    => Option.None
      }

      /** Реализовать метод printIfAny, который будет печатать значение, если оно есть
        */
      def printIfAny(): Unit = map(println)

      /** Реализовать метод zip, который будет создавать Option от пары значений из 2-х Option
        */
      def zip[B](other: Option[B]): Option[(Option[T], Option[B])] = (this, other) match {
        case (Option.Some(a), Option.Some(b)) => Option.Some(Option.Some(a) -> Option.Some(b))
        case _                                => Option.None
      }

      /** Реализовать метод filter, который будет возвращать не пустой Option
        * в случае если исходный не пуст и предикат от значения = true
        */
      def filter(predicate: T => Boolean): Option[T] = map(predicate) match {
        case Option.Some(v) if v => this
        case _                   => Option.None
      }
    }

    object Option {

      case class Some[T](v: T) extends Option[T]

      case object None extends Option[Nothing]
    }

  }

  object list {

    /** Реализовать односвязанный иммутабельный список List
      * Список имеет два случая:
      * Nil - пустой список
      * Cons - непустой, содердит первый элемент (голову) и хвост (оставшийся список)
      */

    sealed trait List[+T] {
      def cons[B >: T](el: B): List[B] = this match {
        case List.::(head, tail) => List.::(el, this)
        case List.Nil            => List.::(el, List.Nil)
      }

      /** Метод cons, добавляет элемент в голову списка, для этого метода можно воспользоваться названием `::`
        */
      def ::[B >: T](el: B): List[B] = cons(el)

      /** Метод mkString возвращает строковое представление списка, с учетом переданного разделителя
        */
      def mkString(separator: String): String = _mkString(separator, this, "")

      @tailrec
      private def _mkString[B >: T](separator: String, list: List[B], result: String): String = list match {
        case List.::(head, tail) => _mkString(separator, tail, s"$result${if (result.nonEmpty) separator else ""}$head")
        case List.Nil            => result
      }

      /** Реализовать метод reverse который позволит заменить порядок элементов в списке на противоположный
        */
      def reverse: List[T] = reverse(this)

      @tailrec
      private def reverse[B](list: List[B], result: List[B] = List.Nil): List[B] = list match {
        case List.::(head, tail) => reverse(tail, head :: result)
        case List.Nil            => result
      }

      /** Реализовать метод map для списка который будет применять некую ф-цию к элементам данного списка
        */
      def map[B](f: T => B): List[B] = {
        @tailrec
        def rec(list: List[T], result: List[B] = List.Nil): List[B] = list match {
          case List.::(head, tail) => rec(tail, f(head) :: result)
          case List.Nil            => result.reverse
        }

        rec(this)
      }

      /** Реализовать метод filter для списка который будет фильтровать список по некому условию
        */
      def filter(f: T => Boolean): List[T] = {
        @tailrec
        def rec(list: List[T], result: List[T] = List.Nil): List[T] = {
          list match {
            case List.::(head, tail) if f(head)  => rec(tail, head :: result)
            case List.::(head, tail) if !f(head) => rec(tail, result)
            case List.Nil                        => result.reverse
          }
        }

        rec(this)
      }

    }

    object List {
      case class ::[A](head: A, tail: List[A]) extends List[A]

      case object Nil extends List[Nothing]

      /** Конструктор, позволяющий создать список из N - го числа аргументов
        * Для этого можно воспользоваться *
        *
        * Например вот этот метод принимает некую последовательность аргументов с типом Int и выводит их на печать
        * def printArgs(args: Int*) = args.foreach(println(_))
        */
      def apply[A](v: A*): List[A] =
        if (v.isEmpty) List.Nil
        else ::(v.head, apply(v.tail: _*))

      /** Написать функцию incList котрая будет принимать список Int и возвращать список,
        * где каждый элемент будет увеличен на 1
        */
      def incList(list: List[Int]): List[Int] = list.map(_ + 1)

      /** Написать функцию shoutString котрая будет принимать список String и возвращать список,
        * где к каждому элементу будет добавлен префикс в виде '!'
        */
      def shoutString(list: List[String]): List[String] = list.map(v => s"!$v")

    }

  }
}
