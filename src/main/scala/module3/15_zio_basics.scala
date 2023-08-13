package module3

import zio._

import java.io.IOException
import scala.concurrent.Future
import scala.io.StdIn
import scala.language.postfixOps
import scala.util.Try



/** **
 * ZIO[-R, +E, +A] ----> R => Either[E, A]
 *
 */


object toyModel {


  /**
   * Используя executable encoding реализуем свой zio
   */

  case class ZIO[-R, +E, +A](run: R => Either[E, A]){ self =>

    def map[B](f: A => B): ZIO[R, E, B] =
      flatMap(a => ZIO(r => Right(f(a))))

    def flatMap[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] =
      ZIO(r => self.run(r).fold(
        e => ZIO.fail(e),
        v => f(v)).run(r)
      )
  }



  /**
   * Реализуем конструкторы под названием effect и fail
   */

  object ZIO{

    def effect[A](a: => A): ZIO[Any, Throwable, A] = ZIO(_ => Try(a).toEither)


    def fail[E](e: E): ZIO[Any, E, Nothing] =
      ZIO(_ => Left(e))
  }



  /** *
   * Напишите консольное echo приложение с помощью нашего игрушечного ZIO
   */

    val echo: ZIO[Any, Throwable, Unit] = for{
      str <- ZIO.effect(StdIn.readLine())
      _ <- ZIO.effect(println(str))
    } yield ()







  type Error
  type Environment


  lazy val _: Task[Int] = ??? // ZIO[Any, Throwable, Int]
  lazy val _: IO[Error, Int] = ??? // ZIO[Any, Error, Int]
  lazy val _: RIO[Environment, Int] = ??? // ZIO[Environment, Throwable, Int]
  lazy val _: URIO[Environment, Int] = ??? // ZIO[Environment, Nothing, Int]
  lazy val _: UIO[Int] = ??? // // ZIO[Any, Nothing, Int]
}

object zioConstructors {


  // не падающий эффект
  val z1: UIO[Int] = ZIO.succeed(7)


  // любой эффект
  val z2: Task[Unit] = ZIO.effect(println("Hello"))

  // не падающий эффект

  val z3: UIO[Unit] = ZIO.effectTotal(println("Hello"))


  // From Future
  val f: Future[Int] = ???
  val z4: Task[Int] = ZIO.fromFuture(ec => f)


  // From try
  lazy val t: Try[String] = ???
  lazy val z5: Task[String] = ZIO.fromTry(t)



  // From option
  lazy val opt : Option[Int] = ???
  lazy val z6: IO[Option[Nothing], Int] = ZIO.fromOption(opt)
  lazy val z7: UIO[Option[Int]] = z6.option
  lazy val z8: IO[Option[Nothing], Int] = z7.some


  type User
  type Address


  def getUser(): Task[Option[User]] = ???
  def getAddress(u: User): Task[Option[Address]] = ???

  val r = for{
    user <- getUser().some
    address <- getAddress(user)
  } yield address



  // From either
  lazy val e: Either[String, Int] = ???
  lazy val z9: IO[String, Int] = ZIO.fromEither(e)
  lazy val z10: UIO[Either[String, Int]] = z9.either
  lazy val z11: IO[String, Int] = z10.absolve




  // From function
  lazy val z12: URIO[String, Unit] = ZIO.fromFunction[String, Unit](str => println(str))

  // особые версии конструкторов

  lazy val _: UIO[Unit] = ZIO.unit
  lazy val _: UIO[Option[Nothing]] = ZIO.none
  lazy val _: UIO[Nothing] = ZIO.never // while(true)
  lazy val _: UIO[Nothing] = ZIO.die(new Throwable(""))
  lazy val _: IO[String, Nothing] = ZIO.fail("Oops")

}



object zioOperators {

  /** *
   *
   * 1. Создать ZIO эффект который будет читать строку из консоли
   */

  lazy val readLine = ???

  /** *
   *
   * 2. Создать ZIO эффект который будет писать строку в консоль
   */

  def writeLine(str: String) = ???

  /** *
   * 3. Создать ZIO эффект котрый будет трансформировать эффект содержащий строку в эффект содержащий Int
   */

  lazy val lineToInt = ???
  /** *
   * 3.Создать ZIO эффект, который будет работать как echo для консоли
   *
   */

  lazy val echo = ???

  /**
   * Создать ZIO эффект, который будет привествовать пользователя и говорить, что он работает как echo
   */

  lazy val greetAndEcho = ???



  // greet and echo улучшенный
  lazy val _: ZIO[Any, Throwable, Unit] = ???


  /**
   * Используя уже созданные эффекты, написать программу, которая будет считывать поочереди считывать две
   * строки из консоли, преобразовывать их в числа, а затем складывать их
   */

  lazy val r1 = ???

  /**
   * Второй вариант
   */

  lazy val r2: ZIO[Any, Throwable, Int] = ???

  /**
   * Доработать написанную программу, чтобы она еще печатала результат вычисления в консоль
   */

  lazy val r3 = ???


  lazy val a: Task[Unit] = ???
  lazy val b: Task[String] = ???

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab1: Task[(Unit, String)] = a.zip(b)

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab2: Task[String] = a.zipRight(b)

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab3: Task[String] = b.zipLeft(a)


  /**
   * Последовательная комбинация эффета b и b, при этом результатом должна быть конкатенация
   * возвращаемых значений
   */
  lazy val ab4: Task[String] = b.zipWith(b)(_ + _)


  /**
    * 
    * Другой эффект в случае ошибки
    */

  lazy val ab5 = a.orElse(b)

  /**
    * 
    * A as B
    */

  lazy val c: Task[Int] = a.as(1)

}
