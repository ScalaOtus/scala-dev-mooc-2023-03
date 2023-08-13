package module3

import zio.{Has, RIO}
import zio.macros.accessible
import emailService.{Email, EmailAddress, EmailService, Html}
import module3.userDAO.UserDAO
import zio.console
import zio.ZLayer
import zio.ZIO
import zio.console.Console

package object userService {

  /**
   * Реализовать сервис с одним методом
   * notifyUser, принимает id пользователя в качестве аргумента и шлет ему уведомление
   * при реализации использовать UserDAO и EmailService
   */

    type UserService = Has[UserService.Service]

    @accessible
    object UserService{
      trait Service{
        def notifyUser(id: UserID): RIO[EmailService with Console, Unit]
      }

      class ServiceImpl(userDAO: UserDAO.Service) extends Service{
        override def notifyUser(id: UserID): RIO[EmailService with Console, Unit] = for{
         // emailService <- ZIO.environment[EmailService].map(_.get)
          user <- userDAO.findBy(UserID(1)).some.orElseFail(new Throwable("User not found"))
          email = Email(user.email, Html("Hello here"))
          _ <- EmailService.sendMail(email)
        } yield ()
      }

      val live: ZLayer[UserDAO, Nothing, UserService] =
        ZLayer.fromService[UserDAO.Service, UserService.Service](dao => new ServiceImpl(dao))

    }







}
