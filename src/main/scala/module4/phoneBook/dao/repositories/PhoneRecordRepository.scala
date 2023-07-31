package module4.phoneBook.dao.repositories

import io.getquill.context.ZioJdbc._
import module4.phoneBook.dao.entities._
import module4.phoneBook.db
import zio.{Has, ULayer, ZLayer}
import io.getquill.Ord

object PhoneRecordRepository {
  val ctx = db.Ctx
  import ctx._

  type PhoneRecordRepository = Has[Service]

  trait Service{
      def find(phone: String): QIO[Option[PhoneRecord]]
      def list(): QIO[List[PhoneRecord]]
      def insert(phoneRecord: PhoneRecord): QIO[Unit]
      def update(phoneRecord: PhoneRecord): QIO[Unit]
      def delete(id: String): QIO[Unit]
  }

  class Impl extends Service {

    val phoneRecordSchema = quote{
      querySchema[PhoneRecord]("""PhoneRecord""")

    }

    val addressSchema = quote{
      querySchema[Address]("""Address""")

    }

    // SELECT x1."id", x1."phone", x1."fio", x1."addressId" 
    // FROM PhoneRecord x1 WHERE x1."phone" = ? ORDER BY x1."fio" ASC LIMIT 1
    def find(phone: String): QIO[Option[PhoneRecord]] = 
      ctx.run(
        phoneRecordSchema.filter(_.phone == lift(phone)).sortBy(_.fio)(Ord.asc)
        .take(1)
      ).map(_.headOption)
    
    // SELECT x."id", x."phone", x."fio", x."addressId" FROM PhoneRecord x
    def list(): QIO[List[PhoneRecord]] = ctx.run(
      phoneRecordSchema
    )
    
    // INSERT INTO PhoneRecord ("id","phone","fio","addressId") VALUES (?, ?, ?, ?)
    def insert(phoneRecord: PhoneRecord): QIO[Unit] = ctx.run(
      phoneRecordSchema.insert(lift(phoneRecord))
    ).unit
    
    // UPDATE PhoneRecord SET "id" = ?, "phone" = ?, "fio" = ?, "addressId" = ? WHERE "id" = ?
    def update(phoneRecord: PhoneRecord): QIO[Unit] = ctx.run(
      phoneRecordSchema.filter(_.id == lift(phoneRecord.id))
      .update(lift(phoneRecord))
    ).unit
    
    // DELETE FROM PhoneRecord WHERE "id" = ?
    def delete(id: String): QIO[Unit] = ctx.run(
      phoneRecordSchema.filter(_.id == lift(id))
      .delete
    ).unit


    // implicit join 
    // SELECT phoneRecord."id", phoneRecord."phone", phoneRecord."fio", phoneRecord."addressId", address."id", address."zipCode", address."streetAddress" 
    // FROM PhoneRecord phoneRecord, Address address 
    // WHERE address."id" = phoneRecord."addressId"
    ctx.run( 
      for{
        phoneRecord <- phoneRecordSchema
        address <- addressSchema if(address.id == phoneRecord.addressId)
      } yield (phoneRecord, address)
    )

    //applicative join

    // SELECT x6."id", x6."phone", x6."fio", x6."addressId", x7."id", x7."zipCode", x7."streetAddress" 
    // FROM PhoneRecord x6 INNER JOIN Address x7 ON x6."addressId" = x7."id"
    ctx.run(
      phoneRecordSchema
      .join(addressSchema).on(_.addressId == _.id)
    )

    // flat join
    // SELECT phoneRecord."id", phoneRecord."phone", phoneRecord."fio", phoneRecord."addressId", x8."id", x8."zipCode", x8."streetAddress" FROM PhoneRecord phoneRecord INNER JOIN Address x8 ON x8."id" = phoneRecord."addressId"
    ctx.run(
      for{
        phoneRecord <- phoneRecordSchema
        address <- addressSchema.join(_.id == phoneRecord.addressId)
      } yield (phoneRecord, address)
    )
    
  }
 
  val live: ULayer[PhoneRecordRepository] = ZLayer.succeed(new Impl)
}
