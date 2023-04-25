package module1

import module1.list.Cons
import module1.opt.Option.zip

import java.util.UUID
import scala.annotation.tailrec
import scala.language.postfixOps



/**
 * referential transparency
 */


 object referential_transparency {

  case class Abiturient(id: String, email: String, fio: String)

  type Html = String

  sealed trait Notification

  object Notification {
    case class Email(email: String, text: Html) extends Notification

    case class Sms(telephone: String, msg: String) extends Notification
  }


  case class AbiturientDTO(email: String, fio: String, password: String)

  trait NotificationService {
    def sendNotification(notification: Notification): Unit

    def createNotification(abiturient: Abiturient): Notification
  }


  trait AbiturientService {

    def registerAbiturient(abiturientDTO: AbiturientDTO): Abiturient
  }

  class AbiturientServiceImpl(val notificationService: NotificationService) extends AbiturientService {
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
      else loop(i - 1, n * acc)
    }
    loop(n, 1)
  }



  /**
   * реализовать вычисление N числа Фибоначчи
   * F0 = 0, F1 = 1, Fn = Fn-1 + Fn - 2
   *
   */


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


 object opt extends App {

  /**
   *
   * Реализовать структуру данных Option, который будет указывать на присутствие либо отсутсвие результата
   */

  case class Some[T](v: T) extends Option[T]
  case object None extends Option[Nothing]

  sealed trait Option[+T] {

    def isEmpty: Boolean = this match {
      case Some(v) => false
      case None => true
    }

    def map[B](f: T => B): Option[B] =
      flatMap(v => Some(f(v)))

    def flatMap[B](f: T => Option[B]): Option[B] = this match {
      case Some(v) => f(v)
      case None => None
    }

    /**
     *
     * Реализовать метод printIfAny, который будет печатать значение, если оно есть
     */
    def printIfAny(): Unit = this match {
      case Some(v) => println(v.toString)
      case _ =>
    }

    /**
     *
     * Реализовать метод filter, который будет возвращать не пустой Option
     * в случае если исходный не пуст и предикат от значения = true
     */
    def filter(cond: T => Boolean): opt.Option[T] = this match {
      case Some(v) => if (cond(v)) Some(v) else None
      case _ => None
    }
  }


  object Option{
    /**
     *
     * Реализовать метод zip, который будет создавать Option от пары значений из 2-х Option
     */
    def zip[T, E](a: Option[T], b: Option[E]): Option[(T, E)] = (a, b) match {
      case (Some(a), Some(b)) => Some((a, b))
      case _ => None
    }
  }

  val someValue = Some("Hello,Scala")
  val noneValue = None

  someValue.printIfAny()
  noneValue.printIfAny()

  println(zip(Some("Hello"), Some(" FP"))) // Some((Hello, FP))

  val cond: String => Boolean = _.length > 5
  println(someValue.filter(cond)) // Some(Hello,Scala)
 }

 object list extends  App {
   /**
    *
    * Реализовать односвязанный иммутабельный список List
    * Список имеет два случая:
    * Nil - пустой список
    * Cons - непустой, содердит первый элемент (голову) и хвост (оставшийся список)
    */

   case class Cons[T](head: T, tail: List[T]) extends List[T]

   case object Nil extends List[Nothing]

   trait List[+T]{
     /**
      * Метод cons, добавляет элемент в голову списка, для этого метода можно воспользоваться названием `::`
      *
      */
     def ::[TT >: T](h: TT): List[T] = this match {
       case Nil => List.apply(h.asInstanceOf[T])
       case Cons(head, Nil) => Cons(h.asInstanceOf[T], Cons(head, Nil))
       case Cons(head, tail) => Cons(h.asInstanceOf[T], Cons(head, tail))
     }

     /**
      * Метод mkString возвращает строковое представление списка, с учетом переданного разделителя
      *
      */
     def mkString(sep: String): String = this match {
       case Nil => ""
       case Cons(head, Nil) => head.toString
       case Cons(head, tail) => head.toString + sep + tail.mkString(sep)
     }

     /**
      *
      * Реализовать метод reverse который позволит заменить порядок элементов в списке на противоположный
      */
     def reverse: List[T] = {
       def reverseHelper(remainigList: List[T], acc: List[T]): List[T] = remainigList match {
         case Nil => acc
         case Cons(head, tail) => reverseHelper(tail, Cons(head, acc))
       }
       reverseHelper(this, Nil)
     }

     /**
      *
      * Реализовать метод map для списка который будет применять некую ф-цию к элементам данного списка
      */

     def map[B](f: T => B): List[B] = this match {
       case Nil => Nil
       case Cons(head, Nil) => Cons(f(head), Nil)
       case Cons(head, tail) => Cons(f(head), tail.map(f))
     }

     /**
      *
      * Реализовать метод filter для списка который будет фильтровать список по некому условию
      */
     def filter(f: T => Boolean): List[T] = this match {
       case Nil => Nil
       case Cons(head, Nil)  => if (f(head)) Cons(head, Nil) else Nil
       case Cons(head, tail) => if (f(head)) Cons(head, tail.filter(f)) else Cons(tail.asInstanceOf[Cons[T]].head, tail.asInstanceOf[Cons[T]].tail)
     }
   }


    object List {
      /**
       * Конструктор, позволяющий создать список из N - го числа аргументов
       * Для этого можно воспользоваться *
       *
       * Например вот этот метод принимает некую последовательность аргументов с типом Int и выводит их на печать
       * def printArgs(args: Int*) = args.foreach(println(_))
       */
      def apply[A](v: A*): List[A] =
        if(v.isEmpty) Nil
        else Cons(v.head, apply(v.tail:_*))

      /**
       *
       * Написать функцию incList котрая будет принимать список Int и возвращать список,
       * где каждый элемент будет увеличен на 1
       */
      def incList(list: List[Int]): List[Int] = list.map(_ + 1)

      /**
       *
       * Написать функцию shoutString котрая будет принимать список String и возвращать список,
       * где к каждому элементу будет добавлен префикс в виде '!'
       */
      def shoutString(list: List[String]): List[String] = list.map("!" + _)
    }

   val l1 = 1 :: Nil
   val l2 = 1 :: List(2)
   val l3 = 1 :: List(2, 3, 4, 5)


   println("--mkString--")
   println(l1.mkString(", "))
   println(l2.mkString(", "))
   println(l3.mkString(", "))

   println("--Reverse--")
   println(Nil.reverse.mkString(", "))
   println(List(1).reverse.mkString(", "))
   println(List(1, 2, 3, 4, 5).reverse.mkString(", "))

   println("--Map--")
   println(List(1, 2, 3).map(_ + 1).mkString(", "))

   println("--Filter--")
   println(List(2).filter(_ > 1).mkString(", "))
   println(List(1, 2, 3).filter(_ > 1).mkString(", "))

   println("--incList--")
   println(List.incList(List(1, 2, 3)).mkString(", "))

   println("--shoutString--")
   println(List.shoutString(List("1", "2", "3")).mkString(", "))




 }