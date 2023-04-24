import module1.{executor, threads}
import module1.threads.ToyFuture

import java.util.concurrent.Executor

object Main {

  def main(args: Array[String]): Unit = {
    println("Hello world " + Thread.currentThread().getName)

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

    def rates = {
      val t1 = threads.getRatesLocation3
      val t2 = threads.getRatesLocation4
      println(t1 + t2)
    }

    implicit val ex: Executor = ???

    val t3 = ToyFuture(10)
    val t4 = ToyFuture(20)

    val r: Unit = t3.onComplete{ i1 =>
      t4.onComplete{ i2 =>
        println(i1 + i2)
      }
    }

    val r2: ToyFuture[Unit] = for{
      i1 <- t3
      i2 <- t4
    } yield println(i1 + i2)




    threads.printRunningTime(rates)

  }
}