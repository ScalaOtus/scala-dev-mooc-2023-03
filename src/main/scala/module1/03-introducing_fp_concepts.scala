package module1

import java.util.UUID
import scala.annotation.tailrec
import java.time.Instant

import scala.language.postfixOps



/**
 * referential transparency
 */


object referential_transparency{

  case class Abiturient(id: String, email: String, fio: String)

  type Html = String

  sealed trait Notification

  object Notification{
    case class Email(email: String, text: Html) extends Notification
    case class Sms(telephone: String, msg: String) extends Notification
  }


  case class AbiturientDTO(email: String, fio: String, password: String)

  trait NotificationService{
    def sendNotification(notification: Notification): Unit
    def createNotification(abiturient: Abiturient): Notification
  }


  trait AbiturientService{

    def registerAbiturient(abiturientDTO: AbiturientDTO): Abiturient
  }

  class AbiturientServiceImpl(val notificationService: NotificationService) extends AbiturientService{
    override def registerAbiturient(abiturientDTO: AbiturientDTO): Abiturient = {
      val notification = Notification.Email("", "")
      val abiturient = Abiturient(UUID.randomUUID().toString, abiturientDTO.email, abiturientDTO.fio)
      //notificationService.sendNotification(notification)
      // save(abiturient)
      abiturient
    }
  }


}


// recursion

object recursion {

  /**
   * Реализовать метод вычисления n!
   * n! = 1 * 2 * ... n
   */

  def fact(n: Int): Int = {
    var _n = 1
    var i = 2
    while (i <= n){
      _n *= i
      i += 1
    }
    _n
  }

  def factRec(n: Int): Int =
    if(n <= 0) 1 else n * factRec(n - 1)

  def factRecTail(n: Int): Int = {
    @tailrec
    def loop(i: Int, acc: Int): Int = {
      if(i <= 0) acc
      else loop(i - 1, i * acc)
    }
    loop(n, 1)
  }

  /**
   * реализовать вычисление N числа Фибоначчи
   * F0 = 0, F1 = 1, Fn = Fn-1 + Fn - 2
   *
   */
  def fib(n: Int): Int = {
    if(n==0) 0
    else if(n==1) 1
    else fib(n-1)+fib(n-2)
  }

  def fibTail(n: Int): Int = {
    def go(n: Int, acc: Int, x: Int): Int = n match {
      case 0 => acc
      case _ => go(n-1, x, acc+x)
    }
    go(n, 0, 1)
  }
}

object hof{


  // обертки

  def logRunningTime[A, B](f: A => B): A => B = a => {
    val start = System.currentTimeMillis()
    val r = f(a)
    val end = System.currentTimeMillis()
    println(end - start)
    r
  }

  def doomy(string: String): Unit = {
    Thread.sleep(1000)
    println(string)
  }

  val lDoomy: String => Unit = logRunningTime(doomy)



  // изменение поведения ф-ции

  val arr = Array(1, 2, 3, 4, 5)

  def isOdd(i: Int): Boolean = i % 2 > 0

  def not[A](f: A => Boolean): A => Boolean = x => !f(x)

  val isEven: Int => Boolean = not(isOdd)

  isOdd(2)
  isEven(3)

  // изменение самой функции

  def partial[A, B, C](a: A, f: (A, B) => C): B => C = b => f(a, b)

  def sum(x: Int, y: Int): Int  = x + y

  val p: Int => Int = sum(2, _)

  p(2) // 4
  p(3) // 5
















  trait Consumer{
    def subscribe(topic: String): LazyList[Record]
  }

  case class Record(value: String)

  case class Request()

  object Request {
    def parse(str: String): Request = ???
  }

  /**
   *
   * (Опционально) Реализовать ф-цию, которая будет читать записи Request из топика,
   * и сохранять их в базу
   */
  def createRequestSubscription() = ???



}






/**
 *  Реализуем тип Option
 */


object opt {

  /**
   *
   * Реализовать структуру данных Option, который будет указывать на присутствие либо отсутсвие результата
   */

  // Animal -> Dog
  // Covariant + отношения переносятся на контейнер
  // Contravariant - отношения переносятся на контейнер наоборот
  // Invariant - нет отношений
  type Dog

  sealed trait Option[+T]{
    def isEmpty: Boolean = this match {
      case Option.Some(v) => false
      case Option.None => true
    }

    def map[B](f: T => B): Option[B] =
      flatMap(v => Option.Some(f(v)))

    def flatMap[B](f: T => Option[B]): Option[B] = this match {
      case Option.Some(v) => f(v)
      case Option.None => Option.None
    }


    /**
     *
     * Реализовать метод printIfAny, который будет печатать значение, если оно есть
     */
    def printIfAny(): Unit = this match {
      case Option.Some(v) => println(v)
      case Option.None => ()
    }

    /**
     *
     * Реализовать метод zip, который будет создавать Option от пары значений из 2-х Option
     */
    def zip[TT >: T](o: Option[TT]): Option[(T, TT)] = this.flatMap(o1 => o.flatMap(o => Option.Some((o1, o))))

    /**
     *
     * Реализовать метод filter, который будет возвращать не пустой Option
     * в случае если исходный не пуст и предикат от значения = true
     */
    def filter(f: T => Boolean): Option[T] = this.flatMap(o => if(f(o)) Option.Some(o) else Option.None)
  }

  object Option{

    case class Some[T](v: T) extends Option[T]
    case object None extends Option[Nothing]
  }
}

object list {
  /**
   *
   * Реализовать односвязанный иммутабельный список List
   * Список имеет два случая:
   * Nil - пустой список
   * Cons - непустой, содердит первый элемент (голову) и хвост (оставшийся список)
   */

  trait List[+T]{
    /**
     * Метод mkString возвращает строковое представление списка, с учетом переданного разделителя
     *
     */
    def mkString(delimetr: String): String = {
      def loop(l: List[T], acc: String): String = l match {
        case List.Nil => acc.substring(1)
        case List.Cons(head, tail) => loop(tail, acc + delimetr + head)
      }
      loop(this, "")
    }

    /**
     *
     * Реализовать метод reverse который позволит заменить порядок элементов в списке на противоположный
     */
    def reverse(): List[T] = {
      def loop(l: List[T], acc: List[T]): List[T] = l match {
        case List.Nil => acc
        case List.Cons(head, tail) => loop(tail, List.::(head, acc))
      }
      loop(this, List())
    }

    /**
     *
     * Реализовать метод map для списка который будет применять некую ф-цию к элементам данного списка
     */
    def map[B](f: T => B): List[B] = {
      def loop(l: List[T], acc: List[B]): List[B] = l match {
        case List.Nil => acc
        case List.Cons(head, tail) => loop(tail, List.::(f(head), acc))
      }
      loop(this, List()).reverse()
    }

    /**
     *
     * Реализовать метод filter для списка который будет фильтровать список по некому условию
     */
    def filter(f: T => Boolean): List[T] = {
      def loop(l: List[T], acc: List[T]): List[T] = l match {
        case List.Nil => acc
        case List.Cons(head, tail) => if(f(head)) loop(tail, List.::(head, acc)) else loop(tail, acc)
      }
      loop(this, List()).reverse()
    }
  }

  object List{
    case class Cons[A](head: A, tail: List[A]) extends List[A]
    case object Nil extends List[Nothing]

    def apply[A](v: A*): List[A] =
      if(v.isEmpty) List.Nil
      else Cons(v.head, apply(v.tail:_*))

    def ::[T](head: T, l: List[T]): List[T] = Cons(head, l)

    /**
     *
     * Написать функцию incList котрая будет принимать список Int и возвращать список,
     * где каждый элемент будет увеличен на 1
     */
    def incList(l: List[Int]): List[Int]=l.map(_+1).reverse()

    /**
     *
     * Написать функцию shoutString котрая будет принимать список String и возвращать список,
     * где к каждому элементу будет добавлен префикс в виде '!'
     */
    def shoutString(l: List[String]): List[String]=l.map("!".concat).reverse()
  }

   List(1, 2, 3, 4)


   /**
     * Метод cons, добавляет элемент в голову списка, для этого метода можно воспользоваться названием `::`
     *
     */

    /**
      * Конструктор, позволяющий создать список из N - го числа аргументов
      * Для этого можно воспользоваться *
      * 
      * Например вот этот метод принимает некую последовательность аргументов с типом Int и выводит их на печать
      * def printArgs(args: Int*) = args.foreach(println(_))
      */
}