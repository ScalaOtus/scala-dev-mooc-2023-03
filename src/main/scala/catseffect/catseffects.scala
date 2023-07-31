package catseffect

import cats.effect.std.{Console, Semaphore, Supervisor}
import cats.effect.unsafe.implicits.global
import cats.effect._
import cats.implicits._

import java.net.URI
import scala.concurrent.Future
import scala.concurrent.duration._
import cats.Monad

object  catseffects {

  def main(args: Array[String]): Unit ={
    implicit val ec = scala.concurrent.ExecutionContext.Implicits.global


    val pure = IO.pure("pure value")
    val sideeffects  = IO.delay(println("111"))
    val mistake = IO.pure(println("error"))

    sideeffects.unsafeRunSync()
    sideeffects.unsafeRunSync()
    mistake.unsafeRunSync()
    mistake.unsafeRunSync()

    val fromEither = IO.fromEither(Left(new Exception("fail")))
    val fromFuture = IO.fromFuture(IO.delay(Future.successful(1)))

    val failing = IO.raiseError(new Exception("error"))
    val never = IO.never

    val future = Future(Thread.sleep(2000)).map(_=>100)

    val async = IO.async_(
      (cb: Either[Throwable,Int] => Unit) =>
        future.onComplete(a => cb(a.toEither))
    )
    async.unsafeRunSync()

    println(async.map(_+200).unsafeRunSync())
    async.flatMap(i => IO.println(i)).unsafeRunSync()
    // Option[A]  F это будет Option
    //Either[MyError, A] F = Either[MyError,?]
  }

}

// no TG
//here we will have fixed IO
object  FilesAndHttpIO extends  IOApp.Simple {
  def readFile(file: String): IO[String] =
    IO.pure("scontent of some file")

  def httpPost(url: String, body: String): IO[Unit] =
    IO.delay(println(s"Post '$url': '$body'"))

  def run: IO[Unit] = for {
    _ <- IO.delay(println("enter file path"))
    path <- IO.readLine
    data <-readFile(path)
    _ <- httpPost("http://mustermann.de", data)
  } yield ()
}

// with TG
trait FileSystem[F[_]] {
  def readFile(path: String): F[String]
}

object FileSystem {
  def apply[F[_]: FileSystem]: FileSystem[F] = implicitly
}

trait HttpClient[F[_]] {
  def postData(irl: String, body: String): F[Unit]
}

object  HttpClient{
  def apply[F[_]: HttpClient]: HttpClient[F] = implicitly
}


trait Console[F[_]] {
  def readline: F[String]
  def printLine(s: String) : F[Unit]
}

object Console{
  def apply[F[_]: Console]: Console[F] = implicitly
}

//1. now add the interpreter
object Interpreter{
  implicit val consoleIO: Console[IO] = new Console[IO] {
    override def readline: IO[String] = IO.readLine

    override def printLine(s: String): IO[Unit] = IO.println(s)
  }

  implicit val fileSystemIO: FileSystem[IO] = new FileSystem[IO] {
    def readFile(path: String): IO[String] = IO.pure(s"some file with some content $path")
  }

  implicit val httpClientIO: HttpClient[IO] = new HttpClient[IO] {
    def postData(url: String, body: String): IO[Unit] = IO.delay(println(s"POST '$url': '$body'"))
  }
}

// bring it together
object FilesAndHttpTF extends IOApp.Simple{
  def program[F[_]: Console: Monad: FileSystem :HttpClient]:F[Unit] =
    for {
      _ <- Console[F].printLine("Enter file path: ")
      path <- Console[F].readline
      data <- FileSystem[F].readFile(path)
      _<- HttpClient[F].postData("sdfsdfs.de", data)
    }yield()

  import  Interpreter.consoleIO
  import  Interpreter.fileSystemIO
  import  Interpreter.httpClientIO
  def run: IO[Unit] = program[IO]

}