package http4sdsl

import cats.data.Kleisli
import cats.effect._
import com.comcast.ip4s.{Host, Port}
import org.http4s.{Http, HttpRoutes, Request, Response}
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

object Restfull {
  val service: HttpRoutes[IO] =
    HttpRoutes.of {
      case GET -> Root / "hello"/ name => Ok(name)
    }

  val serviceOne: HttpRoutes[IO] =
    HttpRoutes.of {
      case GET -> Root / "hello1" / name => Ok("from service one")
    }

  val serviceTwo: HttpRoutes[IO] =
    HttpRoutes.of {
      case GET -> Root / "hello2" / name => Ok("from service two")
    }

  val router = Router("/" -> serviceOne,
  "/api" -> serviceTwo)


  val httpApp = service.orNotFound
  val server = for {
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(httpApp).build
  } yield s

  val server1 = for {
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(router.orNotFound).build
  } yield s

}

object mainServer extends IOApp.Simple{
  def run(): IO[Unit] ={
    //Restfull.server.use(_ => IO.never)
    Restfull.server1.use(_ => IO.never)
  }
}
