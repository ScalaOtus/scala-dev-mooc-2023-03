package module1

import module1.utils.NameableThreads

import java.io.File
import java.util.{Timer, TimerTask}
import java.util.concurrent.{Callable, Executor, ExecutorService, Executors, ForkJoinPool, ThreadFactory, ThreadPoolExecutor}
import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future, Promise, TimeoutException}
import scala.io.{BufferedSource, Source}
import scala.language.{existentials, postfixOps}
import scala.util.{Failure, Success, Try, Using}

object threads {


  // Thread

  class Thread1 extends Thread{
    override def run(): Unit = {
      println("Hello world " + Thread.currentThread().getName)
    }
  }



  def printRunningTime(v: => Unit): Unit = {
    val start = System.currentTimeMillis()
    v
    val end = System.currentTimeMillis()
    println(s"Execution time ${end - start}")
  }

  // rates

  def getRatesLocation1 = async{
    Thread.sleep(1000)
    println("GetRatesLocation1 "  + Thread.currentThread().getName)
  }

  def getRatesLocation2 = async{
    Thread.sleep(2000)
    println("GetRatesLocation2 "  + Thread.currentThread().getName)
  }

  // async

  def async(f: => Unit): Thread = new Thread{
    override def run(): Unit = f
  }

  def async2[A](f: => A): A = {
    var a: A = null.asInstanceOf[A]
    val t = new Thread {
      override def run(): Unit = a = f
    }
    t.start()
    t.join()
    a
  }

  def getRatesLocation3 = async2{
    Thread.sleep(1000)
    println("GetRatesLocation3 "  + Thread.currentThread().getName)
    10
  }

  def getRatesLocation4 = async2{
    Thread.sleep(2000)
    println("GetRatesLocation4 "  + Thread.currentThread().getName)
    20
  }


  // toy future
  class ToyFuture[T] private(v: => T){
    private var r: T = null.asInstanceOf[T]
    private var isCompleted: Boolean = false
    private val q = mutable.Queue[T => _]()

    def map[B](f: T => B): ToyFuture[B] = ???
    def flatMap[B](f: T => ToyFuture[B]): ToyFuture[B] = ???

    def onComplete[U](f: T => U): Unit = {
      if(isCompleted) f(r)
      else q.enqueue(f)
    }

    private def start(executor: Executor) = {
      val t = new Runnable {
        override def run(): Unit = {
          r = v
          isCompleted = true
          while (q.nonEmpty){
            q.dequeue()(r)
          }
        }
      }
      executor.execute(t)
    }
  }

  object ToyFuture{
    def apply[T](v: => T)(implicit executor: Executor) = {
      val f = new ToyFuture(v)
      f.start(executor)
      f
    }
  }

}

object executor {
      val pool1: ExecutorService = Executors.newFixedThreadPool(2, NameableThreads("fixed-pool-1"))
      val pool2: ExecutorService = Executors.newCachedThreadPool(NameableThreads("cached-pool-2"))
      val pool3: ExecutorService = Executors.newWorkStealingPool(4)
      val pool4: ExecutorService = Executors.newSingleThreadExecutor(NameableThreads("singleThread-pool-4"))
}


object future{

}