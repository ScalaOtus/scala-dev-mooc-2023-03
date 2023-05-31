package module3

import zio.ZIO
import zio.Task

import scala.io.StdIn
import scala.util.Try

object zioRecursion {



  /** 
   * Создать ZIO эффект котрый будет читать число в виде строки из консоли 
   */

   lazy val readLine: Task[String] = ZIO.effect(StdIn.readLine())


  /** *
   * Создать ZIO эффект котрый будет трансформировать читающий строку из консоли
      в эффект содержащий Int
   */

  lazy val readInt: Task[Int] = readLine.flatMap(str => ZIO.effect(str.toInt))




  /**
   * Написать программу, которая считывает из консоли Int введнный пользователем,
   * а в случае ошибки, сообщает о некорректном вводе, и просит ввести заново
   *
   */
  lazy val readIntOrRetry: Task[Int] = readInt.orElse{
    ZIO.effect(println("Некорректный ввод, попробуйте еще")) zipRight readIntOrRetry
  }


  /**
   * Считаем факториал
   */



  def factorial(n: BigInt): BigInt = {
    if(n <= 1) n
    else n * factorial(n - 1)
  }

  def fib(n: Long): Long = {
      if(n == 0 || n == 1) n
      else fib(n - 1) + fib(n - 2)
  }

  /**
   * Написать ZIO версию ф-ции факториала
   *
   */
  def factorialZ(n: BigInt): Task[BigInt] = {
    if( n <= 1) ZIO.succeed(n)
    else ZIO.succeed(n).zipWith(factorialZ(n - 1))(_ * _)
  }



  def fibZ(n: Int) = ???


}
