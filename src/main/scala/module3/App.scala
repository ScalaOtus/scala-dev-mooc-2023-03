package module3

import module3.emailService.EmailService
import module3.userDAO.UserDAO
import module3.userService.{UserID, UserService}
import zio.console.{Console, putStrLn}
import zio.duration.durationInt
import zio.{ExitCode, Has, ULayer, URIO, ZIO, ZLayer}

import scala.language.postfixOps

object App {
  def main(args: Array[String]): Unit = {

    // zio.Runtime.default.unsafeRun()

   // toyModel.echo.run()

  }
}

object ZioApp extends zio.App{



  lazy val env0: ZLayer[Any, Nothing, UserService with EmailService] = (UserDAO.live >>> UserService.live ++ EmailService.live)

  lazy val env: ZLayer[Any, Nothing, UserService with UserService with EmailService] = env0

  lazy val app: ZIO[UserService with EmailService with Console, Throwable, Unit] = UserService.notifyUser(UserID(10))



  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    app.provideSomeLayer[Console](env).exitCode
}