import module1.{executor, future, threads, try_}
import module1.threads.ToyFuture
import module2.implicits.{implicit_conversions, implicit_scopes}

import java.util.concurrent.Executor
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Try
import module3.functional_effects.functionalProgram.executableEncoding
import module3.functional_effects.functionalProgram.declarativeEncoding
import module3.zioDS.ref.{updateCounter, updateCounterRef}
import module3.{multipleErrors, toyModel, zioConcurrency, zioConstructors, zioRecursion}
import zio.{ExitCode, URIO, ZIO}

object Main {

  def main(args: Array[String]): Unit = {
//    println("Hello world " + Thread.currentThread().getName)

//    val t1 = new Thread{
//      override def run(): Unit = {
//        Thread.sleep(1000)
//        println("Hello world " + Thread.currentThread().getName)
//      }
//    }
//    val t2 = new Thread{
//      override def run(): Unit = {
//        Thread.sleep(2000)
//        println("Hello world " + Thread.currentThread().getName)
//      }
//    }
//    t2.start()
//    t2.join()
//    t1.start()

//    def rates = {
//      val t1 = threads.getRatesLocation3
//      val t2 = threads.getRatesLocation4
//      println(t1 + t2)
//    }

//    implicit val ex: Executor = ???

//    val t3 = ToyFuture(10)
//    val t4 = ToyFuture(20)

//    val r: Unit = t3.onComplete{ i1 =>
//      t4.onComplete{ i2 =>
//        println(i1 + i2)
//      }
//    }
//
//    val r2: ToyFuture[Unit] = for{
//      i1 <- t3
//      i2 <- t4
//    } yield println(i1 + i2)




//    threads.printRunningTime(rates)

//    try_.readFromFile().foreach(println)

//    Try("").map(_.toInt).foreach(println)

//    import scala.concurrent.ExecutionContext.Implicits.global
//
//
//    def rates2: Future[Int] = {
//      val v1 = future.getRatesLocation1
//      val v2 = future.getRatesLocation2
//      for {
//        r1 <- v1
//        r2 <- v2
//      } yield (r1 + r2)
//    }
//
//
//    def rates3 =
//      future.getRatesLocation1
//        .zip(future.getRatesLocation2)

//    future.printRunningTime(rates3)
//      .foreach(println)

//    Await.result(future.f7, 6 seconds)
   // Thread.sleep(4000)

 //   implicit_scopes.result

 //   val r = declarativeEncoding.interpret(declarativeEncoding.p1)

  //  zio.Runtime.default.unsafeRun(zioConstructors.z11)


    val eff = ZIO.effect{
      println("1")

    }.flatMap(_ => ZIO.effect(println("2")))

    val c = updateCounter
    zio.Runtime.default.unsafeRun(c.map(println))

  //  Thread.sleep(2000)
 //   println(zioRecursion.factorial(10000))
  }
}

object Main2 extends zio.App{
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = zioConstructors.z11.exitCode
}