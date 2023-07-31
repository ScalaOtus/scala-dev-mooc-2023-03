package module4.phoneBook

import module3.emailService.EmailService
import module3.userDAO.UserDAO
import module3.userService.{UserID, UserService}
import zio._
import zio.console.Console


object
Main extends App {

  val app: ZIO[UserService with EmailService with Console, Throwable, Unit] =
    UserService.notifyUser(UserID(10))

  val env: ZLayer[Any, Throwable, UserService with EmailService] =
    UserDAO.live >>> UserService.live ++ EmailService.live

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = 
    app.provideSomeLayer[Console](env).exitCode
}
