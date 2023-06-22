package module3


import scala.io.StdIn

object functional_effects {


  object simpleProgram {

    val greet = {
      println("Как тебя зовут?")
      val name = StdIn.readLine()
      println(s"Привет, $name")
    }

    val askForAge = {
      println("Сколько тебе лет?")
      val age = StdIn.readInt()
      if (age > 18) println("Можешь проходить")
      else println("Ты еще не можешь пройти")
    }


    def greetAndAskForAge = ???


  }


  object functionalProgram {

    /**
     * Executable encoding and Declarative encoding
     */

    object executableEncoding {

      /**
       * 1. Объявить исполняемую модель Console
       */

      case class Console[A](run: () => A){ self =>

        def map[B](f: A => B): Console[B] = flatMap(a => Console.succeed(f(a)))

        def flatMap[B](f: A => Console[B]): Console[B] =
          Console.succeed(f(self.run()).run())
      }


      /**
       * 2. Объявить конструкторы
       */

      object Console{
        def succeed[A](a: => A): Console[A] = Console(() => a)
        def printLine(str: String): Console[Unit] = Console(() => println(str))
        def readLine(): Console[String] = Console(() => StdIn.readLine())
      }


      /**
       * 3. Описать желаемую программу с помощью нашей модель
       */

//      val greet = {
//        println("Как тебя зовут?")
//        val name = StdIn.readLine()
//        println(s"Привет, $name")
//      }

//  val askForAge = {
//    println("Сколько тебе лет?")
//    val age = StdIn.readInt()
//    if (age > 18) println("Можешь проходить")
//    else println("Ты еще не можешь пройти")
//  }
      val greetC: Console[Unit] = for{
          _ <- Console.printLine("Как тебя зовут?")
          name <- Console.readLine()
          _ <- Console.printLine(s"Привет, $name")
      } yield ()

      val askForAgeC: Console[Unit] = for{
        _ <- Console.printLine("Сколько тебе лет?")
        age <- Console.readLine()
        _ <-  if (age.toInt > 18) Console.printLine("Ты можешь пройти") else  Console.printLine("Ты еще не можешь пройти")
      } yield ()

      //val greetAndAsk = greetC.flatMap(_ => askForAgeC)

    }


    object declarativeEncoding {

      /**
       * 1. Объявить декларативную модель Console
       */

      sealed trait Console[A]{
        def map[B](f: A => B): Console[B] =
          FlatMap[A, B](this, a => Console.succeed(f(a)))
        def flatMap[B](f: A => Console[B]): Console[B] =
          FlatMap(this, f)
      }

      case class PrintLine[A](str: String, rest: Console[A]) extends Console[A]
      case class ReadLine[A](f: String => Console[A]) extends Console[A]
      case class Succeed[A](str: () => A) extends Console[A]
      case class FlatMap[A, B](a: Console[A], f: A => Console[B]) extends Console[B]

      object Console{
        def succeed[A](v: => A): Console[A] = Succeed(() => v)
        def printLine(str: String): Console[Unit] = PrintLine(str, succeed(()))
        def readLine(): Console[String] = ReadLine(str => succeed(str))
      }


      /**
       * 2. Написать конструкторы
       * 
       */

      /**
       * 3. Описать желаемую программу с помощью нашей модели
       */

      //      val greet = {
      //        println("Как тебя зовут?")
      //        val name = StdIn.readLine()
      //        println(s"Привет, $name")
      //      }

      val p1 = for{
        _ <- Console.printLine("Как тебя зовут?")
        name <- Console.readLine()
        _ <- Console.printLine(s"Привет, $name")
      } yield ()


      /**
       * 4. Написать операторы
       *
       */

      /**
       * 5. Написать интерпретатор для нашей ф-циональной модели
       *
       */

       def interpret[A, B](console: Console[A]): A = console match {
         case PrintLine(str, rest) =>
           println(str)
           interpret(rest)
         case ReadLine(f) =>
           interpret(f(StdIn.readLine()))
         case Succeed(str) => str()
         case FlatMap(a: Console[A], f: (A => Console[B])) =>
           interpret(f(interpret(a)))
       }


      /**
       * Реализуем туже прошрамму что и в случае с исполняемым подходом
       */

    }

  }

}