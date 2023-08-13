package module4

import com.dimafeng.testcontainers.PostgreSQLContainer
import zio.ZIO
import zio.test.TestAspect._
import zio.Has
import zio.macros.accessible
import zio.RIO
import zio.Task
import zio.ZManaged
import liquibase.Liquibase
import liquibase.resource.FileSystemResourceAccessor
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.CompositeResourceAccessor
import liquibase.database.jvm.JdbcConnection
import module4.phoneBook.db.DataSource
import zio.{ULayer, ZLayer}
import zio.URIO

object MigrationAspects {
  
  def migrate() = {
    before(LiquibaseService.performMigration.orDie)
  }

}

@accessible
object LiquibaseService {

    type Liqui = Has[Liquibase]

    type LiquibaseService = Has[LiquibaseService.Service]


    trait Service {
      def performMigration: RIO[Liqui, Unit]
    }

    class Impl extends Service {

      override def performMigration: RIO[Liqui, Unit] = liquibase.map(_.update("dev"))
    }


  def mkLiquibase(): ZManaged[DataSource, Throwable, Liquibase] = for {
    ds <- ZIO.environment[DataSource].map(_.get).toManaged_
    fileAccessor <-  ZIO.effect(new FileSystemResourceAccessor()).toManaged_
    classLoader <- ZIO.effect(classOf[LiquibaseService].getClassLoader).toManaged_
    classLoaderAccessor <- ZIO.effect(new ClassLoaderResourceAccessor(classLoader)).toManaged_
    fileOpener <- ZIO.effect(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor)).toManaged_
    jdbcConn <- ZManaged.makeEffect(new JdbcConnection(ds.getConnection()))(c => c.close())
    liqui <- ZIO.effect(new Liquibase("src/test/resources/liquibase/main.xml", fileOpener, jdbcConn)).toManaged_
  } yield liqui


    val liquibaseLayer: ZLayer[DataSource, Throwable, Liqui] = ZLayer.fromManaged(mkLiquibase())


    def liquibase: URIO[Liqui, Liquibase] = ZIO.service[Liquibase]

    val live: ULayer[LiquibaseService] = ZLayer.succeed(new Impl)

}