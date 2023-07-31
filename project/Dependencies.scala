import sbt._

object Dependencies {
  lazy val KindProjectorVersion = "0.10.3"
  lazy val kindProjector =
    "org.typelevel" %% "kind-projector" % KindProjectorVersion

  lazy val ZioVersion = "1.0.4"
  lazy val LiquibaseVersion = "3.4.2"
  lazy val PostgresVersion = "42.3.1"
  lazy val LogbackVersion = "1.2.3"
  lazy val ZIOHttpVersion = "1.0.0.0-RC27"
  lazy val CirceVersion = "0.14.2"


  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"

  lazy val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio" % ZioVersion,
    "dev.zio" %% "zio-test" % ZioVersion,
    "dev.zio" %% "zio-test-sbt" % ZioVersion,
    "dev.zio" %% "zio-macros" % ZioVersion
  )

  lazy val zioConfig: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio-config" % "1.0.5",
    "dev.zio" %% "zio-config-magnolia" % "1.0.5",
    "dev.zio" %% "zio-config-typesafe" % "1.0.5"
  )

  lazy val quill = Seq(
    "io.getquill"          %% "quill-jdbc-zio" % "3.12.0",
    "io.github.kitlangton" %% "zio-magic"      % "0.3.11"
  )

  lazy val liquibase = "org.liquibase" % "liquibase-core" % LiquibaseVersion

  lazy val  testContainers = Seq(
    "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.12"  % Test,
    "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.12"  % Test
  )

  lazy val postgres = "org.postgresql" % "postgresql" % PostgresVersion

  lazy val logback = "ch.qos.logback"  %  "logback-classic" % LogbackVersion

  lazy val zioHttp = "io.d11" %% "zhttp" % ZIOHttpVersion

  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core" % CirceVersion,
    "io.circe" %% "circe-parser" % CirceVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-literal" % CirceVersion
  )
}
