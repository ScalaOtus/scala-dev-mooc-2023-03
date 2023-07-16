
scalaVersion := "2.13.8"

name := "scala-dev-mooc-2023-03"
organization := "ru.otus"
version := "1.0"



libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"
libraryDependencies += "co.fs2" %% "fs2-core" % "3.6.1"
libraryDependencies += "co.fs2" %% "fs2-io"   % "3.6.1"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.5"
libraryDependencies ++= Dependencies.zio
libraryDependencies ++= Dependencies.zioConfig
libraryDependencies += Dependencies.scalaTest
scalacOptions += "-Ymacro-annotations"
libraryDependencies += "org.http4s" %% "http4s-client" % "0.23.18"
libraryDependencies += "org.http4s" %% "http4s-dsl" % "0.23.18"
libraryDependencies += "org.http4s" %% "http4s-ember-server" % "0.23.18"
libraryDependencies += "org.http4s" %% "http4s-ember-client" % "0.23.18"
testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
