package module3

import module3.zioDS.ref.{updateCounter, updateCounterRef}
import zio.ZIO
import zio.clock.Clock
import zio.console.{Console, getStrLn, putStrLn}
import zio.duration.durationInt
import zio.random.Random
import zio.test.Assertion.{anything, equalTo, hasSize, isSubtype, throws}
import zio.test.{DefaultRunnableSpec, ZSpec}
import zio.test._
import zio.test.environment._
import zio.test.TestAspect._

import scala.language.postfixOps
import java.io.IOException


object BasicZIOSpec extends DefaultRunnableSpec{

  val greeter: ZIO[zio.console.Console, IOException, Unit] = for{
    _ <- putStrLn("Как тебя зовут")
    name <- getStrLn
    _ <- putStrLn(s"Привет, $name")
    age <- getStrLn
    _ <- putStrLn(s"Age $age")
  } yield ()


  val hello: ZIO[Console with Clock, Nothing, Unit] =
    ZIO.sleep(2 seconds) *> zio.console.putStrLn("Hello").orDie


  val intGen: Gen[Random, Int] = Gen.anyInt

  override def spec = suite("Basic")(
    suite("Arithmetic")(
      test("2*2")(
        assert(2 * 2)(equalTo(4))
      ),
      test("division by zero")(
        assert(2 / 0)(throws(isSubtype[ArithmeticException](anything)))
      )
    ),
    suite("Effect test")(
      testM("simple effect")(
        assertM(ZIO.succeed(2 * 2))(equalTo(4))
      ),
      testM("test console")(
        for{
          _ <- TestConsole.feedLines("Alex", "18")
          _ <- greeter
          value <- TestConsole.output
        } yield{
          assert(value)(hasSize(equalTo(3))) &&
            assert(value.head)(equalTo("Как тебя зовут\n"))
        }
      ),
      testM("test Clock")(
        for{
          fiber <- hello.fork
          _ <- TestClock.adjust(2 seconds)
          _ <- fiber.join
          value <- TestConsole.output
        } yield assert(value(0))(equalTo("Hello\n"))
      ),
      testM("counter")(
        assertM(updateCounterRef)(equalTo(10))
      ) @@nonFlaky
    )
  )
}
