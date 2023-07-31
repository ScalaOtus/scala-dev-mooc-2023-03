package module4

import com.dimafeng.testcontainers.PostgreSQLContainer
import io.getquill.{Escape, JdbcContextConfig, Literal, NamingStrategy, PostgresZioJdbcContext}
import com.typesafe.config.Config
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import zio.ZLayer
import zio.ZIO
import zio.Has
import zio.blocking.Blocking

object DBTransactor {

  type DataSource = Has[javax.sql.DataSource]

  object Ctx extends PostgresZioJdbcContext(NamingStrategy(Escape, Literal))

  def hikariDS(config: Config): HikariDataSource = JdbcContextConfig(config).dataSource

  def test: ZLayer[TestContainer.Postgres with Blocking, Throwable, DataSource] = ZLayer.fromManaged(
      (for {
      pg <- ZIO.service[PostgreSQLContainer].toManaged_
      config <- ZIO.effect{
        val hc = new HikariConfig()
        hc.setUsername(pg.username)
        hc.setPassword(pg.password)
        hc.setJdbcUrl(pg.jdbcUrl)
        hc.setDriverClassName(pg.driverClassName)
        hc
      }.toManaged_
      ds <- ZIO.effect(new HikariDataSource(config)).toManaged_
    } yield ds)
  )

}