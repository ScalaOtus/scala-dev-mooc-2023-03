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
      val pool1: ExecutorService =
        Executors.newFixedThreadPool(2, NameableThreads("fixed-pool-1"))
      val pool2: ExecutorService =
        Executors.newCachedThreadPool(NameableThreads("cached-pool-2"))
      val pool3: ExecutorService =
        Executors.newWorkStealingPool(4)
      val pool4: ExecutorService =
        Executors.newSingleThreadExecutor(NameableThreads("singleThread-pool-4"))
}


object try_{

  def readFromFile(): List[Int] = {
    val s: BufferedSource = Source.fromFile(new File("ints.txt"))
    val result: List[Int] = try{
      s.getLines().map(_.toInt).toList
    } catch {
      case e =>
        println(e.getMessage)
        Nil
    } finally {
      s.close()
    }
    result
  }

  def readFromFile2(): Try[List[Int]] = {
    val source: Try[BufferedSource] = Try(Source.fromFile(new File("ints.txt")))

    def lines(s: BufferedSource): Try[List[Int]] =
      Try(s.getLines().map(_.toInt).toList)

    val result = for{
      s <- source
      r <- lines(s)
    } yield r
    source.foreach(_.close())
    result
  }

  readFromFile2() match {
    case Failure(exception) =>
    println(exception.getMessage)
    case Success(value) =>
      value.foreach(println)
  }

  readFromFile2().foreach(l => l.foreach(println))

  val rr = try {
    println("111")
    try {
      throw new Exception("exception 2")
    } catch{
      case ex => println(ex.getMessage)
    }
  } catch {
    case ex => println(ex.getMessage)
  }
}

object future{
  // constructors

  def longRunningComputation: Int = ???
  lazy val f1: Future[Int] = Future.successful(10)
  lazy val f2: Future[Nothing] = Future.failed(new Exception("oops"))
  lazy val f3: Future[Int] = Future.fromTry(Try(10))
  lazy val f4: Future[Int] = Future(longRunningComputation)(scala.concurrent.ExecutionContext.global)



  // combinators

  //import scala.concurrent.ExecutionContext.Implicits.global

  def getRatesLocation1 = Future{
    Thread.sleep(1000)
    println("GetRatesLocation1 "  + Thread.currentThread().getName)
    10
  }(scala.concurrent.ExecutionContext.global)

  def getRatesLocation2 = Future{
    Thread.sleep(2000)
    println("GetRatesLocation2 "  + Thread.currentThread().getName)
    20
  }(scala.concurrent.ExecutionContext.global)

  getRatesLocation1.foreach(println)(scala.concurrent.ExecutionContext.global)

  val _: Future[Int] = getRatesLocation1.recover{
    case e => 0
  }(scala.concurrent.ExecutionContext.global)


  def printRunningTime[T](v: => Future[T]): Future[T] = {
    implicit val foo = scala.concurrent.ExecutionContext.global
    for{
      start <- Future.successful(System.currentTimeMillis())
      r <- v
      end <- Future.successful(System.currentTimeMillis())
      _ <- Future.successful(println(s"Execution time ${end - start}"))
    } yield r
  }






  // Execution contexts
  lazy val ec = ExecutionContext.fromExecutor(executor.pool1)
  lazy val ec2 = ExecutionContext.fromExecutor(executor.pool2)
  lazy val ec3 = ExecutionContext.fromExecutor(executor.pool3)
  lazy val ec4 = ExecutionContext.fromExecutor(executor.pool4)

  def action(v: Int): Int = {
    Thread.sleep(1000)
    println(s"Action ${v} in ${Thread.currentThread().getName}")
    v
  }

  val f5 = Future(action(10))(ec)
  val f6 = Future(action(20))(ec2)

  val f7 = f5.flatMap{ v1 =>
    action(50)
    f6.map{ v2 =>
      action(v1 + v2)
    }(ec4)
  }(ec3)


}

object promise {

//  val p1: Promise[Int] = Promise[Int]
//  p1.isCompleted // false
//  val f1: Future[Int] = p1.future
//  f1.isCompleted // false
//
//  f1.onComplete()
//
//  p1.complete(Try(10))


  object FutureSyntax {


    def map[T, B](future: Future[T])(f: T => B)(implicit ec: ExecutionContext): Future[B] =
      flatMap(future)(v => make(f(v)))

    def flatMap[T, B](future: Future[T])(f: T => Future[B])(implicit ec: ExecutionContext): Future[B] = {
      val p = Promise[B]
      future.onComplete {
        case Failure(exception) => p.failure(exception)
        case Success(value) => f(value).onComplete {
          case Failure(exception) => p.failure(exception)
          case Success(value) => p.success(value)
        }
      }
      p.future
    }


    def make[T](v: => T)(implicit ec: ExecutionContext): Future[T] = {
      val p = Promise[T]
      val r = new Runnable {
        override def run(): Unit = p.complete(Try(v))
      }
      ec.execute(r)
      p.future
    }

    def make[T](v: => T, timeout: Long): Future[T] = {
      val p = Promise[T]
      val timer = new Timer(true)
      val task = new TimerTask {
        override def run(): Unit = ???
      }
      timer.schedule(task, timeout)
      ???
    }
  }
}