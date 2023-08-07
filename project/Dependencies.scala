import sbt.Keys.libraryDependencies
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
  lazy val CirceVersion = "0.14.1"


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

  lazy val akkaVersion = "2.8.3"
  lazy val leveldbVersion = "0.7"
  lazy val leveldbjniVersion = "1.8"
  lazy val akkaContainers = Seq(
    // Use Coda Hale Metrics and Akka instrumentation
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "io.aeron" % "aeron-driver" % "1.40.0",
    "io.aeron" % "aeron-client" % "1.40.0",

    "org.iq80.leveldb" % "leveldb" % leveldbVersion,
    "org.fusesource.leveldbjni" % "leveldbjni-all" % leveldbjniVersion,

    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.1.0" % Test
  )


  lazy val postgres = "org.postgresql" % "postgresql" % PostgresVersion

  lazy val logback = "ch.qos.logback"  %  "logback-classic" % LogbackVersion

  lazy val zioHttp = "io.d11" %% "zhttp" % ZIOHttpVersion

  lazy val circe = Seq(
    "io.circe" %% "circe-core" % CirceVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-parser" % CirceVersion,
    "io.circe" %% "circe-derivation" % "0.13.0-M4",
    "org.http4s" %% "http4s-circe" % "0.23.14"
  )
}
