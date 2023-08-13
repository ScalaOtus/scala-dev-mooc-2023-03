package catsconcurrency

import cats.implicits._
import cats.effect.{Deferred, IO, IOApp, Ref, Resource}
import cats.Alternative.ops.toAllAlternativeOps

import scala.util.Try
import cats.syntax.either._


object MainCatsConcurrent extends  IOApp.Simple {
  final case class Environment(counter: Ref[IO, Int], startGun: Deferred[IO, Unit])
  def buildEnv: Resource[IO, Environment] =(
    Resource.make(IO.println("Alloc. counter") *> Ref.of[IO,Int](0))( _ =>
      IO.println("dealloc. counter")), Resource.make(IO.println("Alloc. gun") *> Deferred[IO, Unit])(
      _ => IO.println("Dealloc. gun"))
    ).parMapN{ case ( counter, gun) => Environment(counter, gun) }



  def program(env: Environment): IO[Unit] = itertaion(env).iterateWhile(a=>a).void

  def itertaion(env: Environment): IO[Boolean] = for {
    cmd <- IO.print("> ") *> IO.readLine
    shouldProceed <- Command.parse(cmd) match {
      case Left(err) => IO.raiseError(new Exception(s"invalid command $err"))
      case Right(command) => process(env)(command)
    }
  } yield shouldProceed





  def process(env: Environment)(command: Command): IO[Boolean] =
    command match {
      case Command.Echo => {
        IO.readLine.flatMap(text => IO.println(text)).as(true) //map(_=>true)
      }
      case Command.Exit => {
        IO.println("Bye Bye").as(false)
      }
      case Command.AddNumber(num) => env.counter.updateAndGet(_ + num).flatMap(IO.println).as(true)
      case Command.ReadNumber => env.counter.get.flatMap(IO.println).as(true)
      case Command.LaunchDog(name) =>
        val fiberIO = (IO.println(s"Dog $name is ready")) *> env.startGun.get *>
          IO.println(s"Dog $name is starting") *> env.counter.updateAndGet(_+1)
          .flatMap(value => IO.println(s"Dog $name observe value $value"))
        fiberIO.start.as(true)


      case Command.ReleaseTheDogs => env.startGun.complete()
    }

  def run: IO[Unit] =
    buildEnv.use(env => program(env))

}


import cats.syntax.all._
sealed trait Command extends Product with Serializable

object Command {
  //echo
  case object Echo extends Command
  //exit
  case object Exit extends Command

  case class  AddNumber(num:Int) extends Command

  case object  ReadNumber extends Command

  case class  LaunchDog(name: String) extends  Command

  case object  ReleaseTheDogs extends  Command

  def parse(s: String): Either[String, Command] =
    s.toLowerCase match {
      case "echo" => Right(Echo)
      case "exit" => Right(Exit)
      case "dogs" => Right(ReleaseTheDogs)
      case "read-number" => Right(ReadNumber)
      case cmd =>

        cmd.split(" ").toList match {
          case  List("l", dogName) =>
            Right(LaunchDog(dogName))
          case  List("n", IntString(num)) =>
            Right(AddNumber(num))
          case _ => Left("not valid command")
        }
    }

  private  object  IntString {
    def unapply(s: String): Option[Int] =
      Try(s.toInt).toOption
  }



}