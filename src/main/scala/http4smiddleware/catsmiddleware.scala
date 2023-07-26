package http4smiddleware

import cats.Functor
import cats.data.{Kleisli, OptionT}
import cats.effect.{IO, IOApp, Ref, Resource}
import org.http4s.{AuthedRequest, AuthedRoutes, HttpRoutes, Method, Request, Response, Status, Uri}
import org.http4s.Method.GET
import org.http4s.dsl.io._
import org.http4s.implicits._
import cats.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.{AuthMiddleware, HttpMiddleware, Router}
import com.comcast.ip4s.{Host, Port}
import http4sdsl.Restfull
import http4smiddleware.Restfull1.User
import org.typelevel.ci.CIStringSyntax

object Restfull1 {

  val serviceOne: HttpRoutes[IO] = HttpRoutes.of {
    case GET -> Root / "hello1" / name => Ok(s"from service1 $name")
  }

  val serviceTwo: HttpRoutes[IO] = HttpRoutes.of {
    case GET -> Root / "hello2" / name => Ok(s"from service2 $name")
  }

  def LoginService(sessions: Sessions[IO]): HttpRoutes[IO] =
    HttpRoutes.of {
      case PUT -> Root / "login" / name =>
        sessions.update(set => set+name).flatMap(_=>Ok("done"))
    }

  def serviceHello: HttpRoutes[IO] =
    HttpRoutes.of {
      case r@GET -> Root / "hello" =>
        r.headers.get(ci"X-User-Name") match {
          case Some(values) =>
            val name = values.head.value
            Ok(s"Hello, $name")
          case None => Forbidden("you shall not pass!!!")
        }
    }

  def serviceHelloAuth: AuthedRoutes[User, IO] = AuthedRoutes.of {
    case GET -> Root / "hello" as user =>
      Ok(s"Hello, ${user.name}")
  }

  def addresponseHeaderMiddleware[F[_]: Functor](routes: HttpRoutes[F]): HttpRoutes[F] = Kleisli { req =>
    val maybeResponse = routes(req)

    maybeResponse.map{
      case Status.Successful(res) => res.putHeaders("X-Otus" -> "Hello")
      case other => other
    }
  }

  final case class User(name: String)

  def serviceAuthMiddleware(sessions: Sessions[IO]): AuthMiddleware[IO, User] =
    authRoutes =>
      Kleisli{ req =>
        req.headers.get(ci"X-User-Name") match {
          case Some(values) =>
            val name = values.head.value

            for {
              users <- OptionT.liftF(sessions.get)
              results <-
                if (users.contains(name)) authRoutes(AuthedRequest(User(name), req))
                else OptionT.liftF(Forbidden("you shall not pass!!!"))

            } yield results

          case None => OptionT.liftF(Forbidden("you shall not pass!!!"))


        }
      }


  //1. router
  val router = addresponseHeaderMiddleware(Router("/" -> addresponseHeaderMiddleware(serviceOne), "/api" -> addresponseHeaderMiddleware(serviceTwo)))

  def routerSessions(sessions: Sessions[IO]) = addresponseHeaderMiddleware(Router("/" -> serviceSessions(sessions)))

  def routerSessionAuth(sessions: Sessions[IO]) =
    addresponseHeaderMiddleware(Router("/" -> (LoginService(sessions) <+> serviceAuth(sessions)(serviceHello))))

  def routerSessionAuthClear(sessions: Sessions[IO]) =
    addresponseHeaderMiddleware(Router("/" -> (LoginService(sessions) <+> serviceAuthMiddleware(sessions)(serviceHelloAuth))))


  type Sessions[F[_]] = Ref[F, Set[String]]
  def serviceSessions(sessions: Sessions[IO]): HttpRoutes[IO] =
    HttpRoutes.of {
      case r@GET -> Root / "hello" =>
        r.headers.get(ci"X-User-Name") match {
          case Some(values) =>
            val name = values.head.value
            sessions.get.flatMap(users =>
             if (users.contains(name)) Ok(s"Hello, $name")
             else Forbidden("you shall not pass!!!")
            )
          case None => Forbidden("you shall not pass!!!")
        }
      case PUT -> Root / "login" /name =>
        sessions.update(set => set +  name).flatMap(_ => Ok("done"))
    }

  //4. auth
  def serviceAuth(sessions: Sessions[IO]): HttpMiddleware[IO] =
    routes =>
      Kleisli{ req =>
        req.headers.get(ci"X-User-Name") match {
          case Some(values) =>
            val name = values.head.value

            for {
              users <- OptionT.liftF(sessions.get)
              results <-
                if (users.contains(name)) routes(req)
                else OptionT.liftF(Forbidden("you shall not pass!!!"))
            } yield results

          case None => OptionT.liftF(Forbidden("you shall not pass!!!"))
        }
      }



  val server1 = for {
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(router.orNotFound).build
  } yield s


  val serverSessions = for {
    sessions <- Resource.eval(Ref.of[IO, Set[String]](Set.empty))
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(routerSessions(sessions).orNotFound).build
  } yield s

  val serverSessionsAuth = for {
    sessions <- Resource.eval(Ref.of[IO, Set[String]](Set.empty))
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(routerSessionAuth(sessions).orNotFound).build
  } yield s

  val serverSessionsAuthClear = for {
    sessions <- Resource.eval(Ref.of[IO, Set[String]](Set.empty))
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(routerSessionAuthClear(sessions).orNotFound).build
  } yield s
}

object mainServer extends IOApp.Simple {
  def run(): IO[Unit] ={
//    Restfull1.server1.use(_ => IO.never)
//    Restfull1.serverSessions.use(_ => IO.never)
 //   Restfull1.serverSessionsAuth.use(_ => IO.never)
    Restfull1.serverSessionsAuthClear.use(_ => IO.never)
  }
}

//test
/*
object Test extends IOApp.Simple {
  def run: IO[Unit] = {
    val service = Restfull1.serviceHelloAuth

    for {
      result <- service(AuthedRequest(User("abc"), Request(method = Method.GET,
        uri = Uri.fromString("/hello").toOption.get))).value
      _ <- result match {
        case Some(resp) =>
//          resp.bodyText.compile.last.flatMap(body => IO.)
        case None => IO.println("fail")


      }
       yield()
    }


  }


}
*/