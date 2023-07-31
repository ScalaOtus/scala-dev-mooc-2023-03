package http4sdsl

import cats.Functor
import cats.data.{EitherT, OptionT, ReaderT}
import cats.effect.IO
import cats.effect.unsafe.implicits.global


object  catseffects {
  def getUserName: IO[Option[String]] = IO.pure(Some("username"))
  def getId(name: String): IO[Option[Int]] = IO.pure(Some(42))
  def getPermissions(i: Int): IO[Option[String]] = IO.pure(Some("permissions"))

/*  def compose[F[_]: Functor, G[_]: Functor]: Functor[X=> F[G[X]]] = new Functor[X => F[G[X]]] {
    override def map[A, B](fa: F[G[A]] => A)(f: A => B): F[G[B]] => B = Functor[F].map(ga=> Functor[G](....))
  }
  */

  def main(args: Array[String]): Unit = {
    implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
/*
    val res = for {
      username <- getUserName
      id <- getId(username)
    }yield()
 */

    val res: OptionT[IO, (String, Int, String)] = for {
      username <- OptionT(getUserName)
      id <- OptionT(getId(username))
      permissions <- OptionT(getPermissions(id))
    } yield (username, id, permissions)

    //println(res.value.unsafeRunSync())
    def getUserName1: IO[Option[String]] = IO.pure(Some("username"))
    def getId1(name: String): IO[Int] = IO.pure(42)
    def getPermissions1(i: Int): IO[Option[String]] = IO.pure(Some("permissions"))

    val res1: OptionT[IO, (String, Int, String)] = for {
      username <- OptionT(getUserName1)
      id <- OptionT.liftF(getId1(username))
      permissions <- OptionT(getPermissions1(id))
    } yield (username, id, permissions)

    //EitherT
    sealed trait UserServiceError
    case class PermissionDenied(msg: String) extends UserServiceError
    case class UserNotFound(userId: Int) extends UserServiceError
    def getUserName2(userId: Int): EitherT[IO, UserServiceError, String] = EitherT.pure("user name 1")
    def getUserAddress(userId: Int): EitherT[IO, UserServiceError, String] =
      EitherT.fromEither(Left(PermissionDenied("bla bla bla")))

    def getProfile(id: Int) = for {
      name <- getUserName2(id)
      address <- getUserAddress(id)
    } yield (name, address)

    println(getProfile(2).value.unsafeRunSync())

    //readerT
    // A => B это тоже монада
    //Reader: A => B ReaderT: A =>F[B]

    trait ConnectionPool
    case class Environment(cp: ConnectionPool)
    def getUserAliac(): ReaderT[IO,Environment,String] = ReaderT(cp=>IO.pure("sdfsdf"))
    def getComment(): ReaderT[IO,Environment,String] = ReaderT.liftF(IO.pure("sdfsdf"))
    def updateComment(): ReaderT[IO,Environment,String] = ReaderT.liftF(IO.pure("updated"))

    val result = for {
      a <- getUserAliac()
      b <- getComment()
      _ <- updateComment()
    } yield (a,b)
    result(Environment(new ConnectionPool {})).unsafeRunSync()



  }

}