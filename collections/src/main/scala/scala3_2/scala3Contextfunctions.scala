package scala3_2

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import concurrent.duration.DurationInt
import concurrent.duration.DurationLong
import concurrent.duration.DurationDouble
object scala3Contextfunctions {
  type Executable[T]  = ExecutionContext ?=> T

  def f1(n:Int): Executable[Future[Int]] = f2(n+1)
  def f2(n:Int): Executable[Future[Int]] = f3(n+1)
  def f3(n:Int): Executable[Future[Int]] = f4(n+1)
  def f4(n:Int): Executable[Future[Int]] = {
    val ex = summon[ExecutionContext]
    Future {
      println(s"hi from the future, n is $n")
      n
    }
  }

  @main  def scala3ContextfunctionsEx()={
    given ec: ExecutionContext = scala.concurrent.ExecutionContext.global
    Await.result(f1(10), 1 second)
  }

}