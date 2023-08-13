package module3.emailService

import zio.test.mock.mockable
import zio.test.mock
import zio.{Has, URLayer}
import zio.ZLayer
import zio.URIO
import zio.UIO
import zio.console.Console

object EmailServiceMock extends mock.Mock[EmailService]{


  object SendMail extends Effect[Email, Nothing, Unit]

  val compose: URLayer[Has[mock.Proxy], EmailService] = ZLayer.fromService{ proxy =>
    new EmailService.Service {
      override def sendMail(email: Email): URIO[Console, Unit] =
        proxy(SendMail, email)

    }
  }
}