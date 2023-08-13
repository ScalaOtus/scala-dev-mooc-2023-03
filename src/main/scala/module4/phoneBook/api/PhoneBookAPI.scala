package module4.phoneBook.api

import module4.phoneBook.dto.PhoneRecordDTO
import module4.phoneBook.services.PhoneBookService
import zhttp.http._
import zio.ZIO
import io.circe.syntax._

object PhoneBookAPI {

  val api = Http.collectZIO[Request]{
    case Method.GET -> !! / phone =>
      PhoneBookService.find(phone).foldM(
        err => ZIO.succeed(Response.status(Status.NotFound)),
        result => ZIO.succeed(Response.json(result.asJson.toString()))
      )
    case req @ Method.POST -> !!  =>
      (for{
        r <- req.body
        dto <- ZIO.fromEither(PhoneRecordDTO.decoder.decodeJson(r.asJson))
        result <- PhoneBookService.insert(dto)
      } yield result).foldM(
        err => ZIO.succeed(Response.status(Status.BadRequest)),
        result => ZIO.succeed(Response.json(result))
      )
    case req @ Method.PUT -> !! / id / addressId => (for{
        r <- req.bodyAsString
        dto <- ZIO.fromEither(PhoneRecordDTO.decoder.decodeJson(r.asJson))
        _ <- PhoneBookService.update(id, addressId, dto)
        } yield ()).foldM(
          err => ZIO.succeed(Response.status(Status.BadRequest)),
          result => ZIO.succeed(Response.ok)
      )
    case Method.DELETE -> !! / id => PhoneBookService.delete(id).foldM(
        err => ZIO.succeed(Response.status(Status.BadRequest)),
        result => ZIO.succeed(Response.ok)
      )
  }
}
