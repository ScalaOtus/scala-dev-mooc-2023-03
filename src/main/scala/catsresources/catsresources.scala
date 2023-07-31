package catsresources

import cats.MonadError
import cats.data.State
import cats.implicits._
import cats.effect.{Clock, Fiber, IO, IOApp, Spawn}
import cats.effect.unsafe.implicits.global
import cats.effect.kernel._

import scala.concurrent.duration._


object catsresources {
  def main(args: Array[String]): Unit ={
    val optionF = for {
      a <- Some(1)
      b <- Some(1)
      c <- None
      d <- Some(1)
    } yield (a + b + c + d)

    val optionF1 = for {
      a <- Right(1)
      b <- Right(1)
      c <- Left("error")
      d <- Right(1)

    } yield a+b+c+d

    type MyMonadError[F[_]] = MonadError[F, String]

    //1
    def withErrorHandling[F[_]: MyMonadError] = for {
      a <- MonadError[F, String].pure(10)
      b <- MonadError[F, String].pure(10)
      c <- MonadError[F, String].pure(10)
      d <- MonadError[F, String].pure(10)
    } yield (a+b+c+d)

    type StringError[A] = Either[String, A]

//    println(withErrorHandling[StringError])

    //2
    def withErrorhandling1[F[_]: MyMonadError]: F[Int] = for {
      a <- MonadError[F, String].pure(10)
      b <- MonadError[F, String].pure(10)
      c <- MonadError[F, String].raiseError[Int]("fail")
      d <- MonadError[F, String].pure(10)
    } yield (a+b+c+d)

    //println(withErrorhandling1[StringError])

//    println(withErrorhandling1.handleError(error => 42))

    //3
    def withErrorhandling2[F[_]: MyMonadError]: F[Int] = for {
      a <- MonadError[F, String].pure(10)
      b <- MonadError[F, String].pure(10)
      c <- MonadError[F, String].raiseError[Int]("fail")
        .handleError(error => 42)
      d <- MonadError[F, String].pure(10)
    } yield (a+b+c+d)
  //  println(withErrorhandling2)

    //4
    def withErrorAttempt[F[_]: MyMonadError]: F[Either[String, Int]] =
      withErrorhandling1[F].attempt

    val nonFailing = IO.raiseError(new Exception("sdf")).attempt

    //val failing = IO.raiseError(new Exception("sdf"))

//    failing *> IO.println("asf")


    // monad cancel
    val b  = IO.println(42) !> IO.raiseError(new Exception("sdf")) !> IO.println(10)
  //  b.unsafeRunSync()

    val justSleep = IO.sleep(1.second) *> IO.println("not cancelled")
    val justSleepAnfdThrow = IO.sleep(100.millis) *> IO.raiseError(new Exception("error"))
    //(justSleep, justSleepAnfdThrow).parTupled.unsafeRunSync()

    val justSleepUncancellable = (IO.sleep(1.second)*> IO.println("not cancelled") ).uncancelable
    (justSleepUncancellable, justSleepAnfdThrow).parTupled.unsafeRunSync()
  }

}



object SpawnApp extends IOApp.Simple {
  def longRunningIO(): IO[Unit] =
    (
      IO.sleep(200.millis) *>
        IO.println(s"hi from thread ${Thread.currentThread}").iterateWhile(_ => true)
      )


  def longRunningIORef(r: Ref[IO, Int]): IO[Unit] =
    (
      IO.sleep(200.millis) *>
        IO.println(s"hi from thread ${Thread.currentThread}").iterateWhile(_ => true)
      )


  def run: IO[Unit] = for {
    r <- Ref.of[IO, Int](10)
    fiber1 <- Spawn[IO].start(longRunningIORef(r))
    fiber2 <- Spawn[IO].start(longRunningIO)
    fiber3 <- Spawn[IO].start(longRunningIO)

    _ <- IO.println("the fibers has been started")
    _ <- IO.sleep(2.second)

    _ <- fiber1.cancel
    _ <- fiber2.cancel
    _ <- IO.sleep(3.second)

  } yield ()

/*  def  run: IO[Unit] = for {
    fiber <- Spawn[IO].start(longRunningIO())
    _ <- IO.println("the fibers has been started")
    _ <- IO.sleep(3.second)

  } yield()*/

}

