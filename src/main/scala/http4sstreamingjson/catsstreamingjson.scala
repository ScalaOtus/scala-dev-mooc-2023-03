package http4sstreamingjson

import cats.effect
import cats.effect.std.Queue
import cats.effect.{IO, IOApp, Resource, SyncIO}
import fs2.{Pure, Stream}
import cats.effect.unsafe.implicits.global
import http4smiddleware.Restfull1
import org.http4s.{Request, Response, Uri}
import org.http4s.ember.client.EmberClientBuilder

import scala.concurrent.duration._
import java.time.Instant


object  HttpClient {
  val builder = EmberClientBuilder.default[IO].build
  val request = Request[IO](uri = Uri.fromString("http://localhost:8080/hello").toOption.get)
  //1

  /*val result: Resource[IO, Response[IO]] = for {
    client <- builder
    response <- client.run(request)
  } yield response
*/
  //2
/*
  val result: Resource[IO, String] = for {
    client <- builder
    response <- effect.Resource.eval(client.expect[String](request))
  } yield response*/

  //3
  val result = builder.use(
    client => client.run(request).use(
      resp => if (!resp.status.isSuccess)
        resp.body.compile.to(Array).map(new String(_))
      else
        IO("Error")
    )
  )
}

object mainServer extends  IOApp.Simple{
  def run(): IO[Unit] = {
    /*for {
      fiber <- Restfull1.serverSessionsAuthClear.use( _ => IO.never).start
      _ <- HttpClient.result.use(IO.println)
      //_ <- fiber.join
    }yield()*/

    for {
      _ <- Restfull1.serverSessionsAuthClear.use(_ => HttpClient.result.flatMap(IO.println) *> IO.never)
    } yield()
  }
}