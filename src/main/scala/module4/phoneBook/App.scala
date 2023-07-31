package module4.phoneBook

import module4.phoneBook.api.PhoneBookAPI
import module4.phoneBook.configuration._
import module4.phoneBook.dao.repositories.{AddressRepository, PhoneRecordRepository}
import module4.phoneBook.db._
import module4.phoneBook.services.PhoneBookService
import zio.blocking.Blocking
import zio.clock.Clock
import zio.random.Random

object App {

    type AppEnvironment = PhoneBookService.PhoneBookService with 
        PhoneRecordRepository.PhoneRecordRepository with 
        AddressRepository.AddressRepository 
        with Configuration with 
        Clock with Blocking with LiquibaseService.Liqui with 
        LiquibaseService.LiquibaseService 
        with Random with DataSource


    val appEnvironment = Configuration.live >+> Blocking.live >+> zioDS >+> 
    LiquibaseService.liquibaseLayer ++ 
    PhoneRecordRepository.live >+> AddressRepository.live >+> 
    PhoneBookService.live ++ LiquibaseService.live

    val httpApp = PhoneBookAPI.api

    val server = (LiquibaseService.performMigration *> 
    zhttp.service.Server.start(8080, httpApp))
    .provideCustomLayer(appEnvironment)
}
