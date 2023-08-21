package scala3_2

import scala3_2.homework2.CompletionArg.{IntWrapper, StringWrapper}
import scala3_2.homework3.Logarithm

import javax.annotation.processing.Completions
import scala.annotation.targetName
import scala.language.implicitConversions

object homework1 {
  extension (x: String)
    //я не нашел как подложить в s3 замену для Predef метода строк, поэтому просто взял ++ метод
    def ++(y: String): Int = {
      if (!(x + y).forall(_.isDigit)) throw new IllegalArgumentException("Method ++ may be appy only for int numbers")
      (x.toInt + y.toInt)
    }

  @main def part1Ex(): Unit = {
    val a1 = "1" ++ "234"
    assert(a1 == 235)
  }

}

object homework2 {
  enum CompletionArg {
    case StringWrapper(in: String)
    case IntWrapper(in: Int)
    case FloatWrapper(in: Float)
  }

  object CompletionArg {
    given fromString: Conversion[String, CompletionArg]  = StringWrapper(_)
    given fromInt: Conversion[Int, CompletionArg] = IntWrapper(_)
    given fromFloat: Conversion[Float, CompletionArg]  = FloatWrapper(_)
  }


  //тут типизация не нужна, пока на входе не контейнер = ) поэтому убрал [T]
  def complete(arg: CompletionArg)  = arg match
    case StringWrapper(s)  => s
    case IntWrapper(in)    =>  in
    case CompletionArg.FloatWrapper(in)  => in




  @main def part2Ex(): Unit = {
    assert(complete("test") == "test")
    assert(complete(1) == 1)
    assert(complete(7f) == 7f)

  }
}


object homework3 {
  opaque type Logarithm = Double

  object Logarithm:
    def apply(d: Double): Logarithm = math.log(d)
    def safe(d: Double): Option[Logarithm] =
      if d > 0.0 then Some(math.log(d)) else None

  end Logarithm


  case class LogoWrapper(val it: Logarithm) extends AnyVal {
    def + (that: Logarithm): Logarithm = Logarithm(math.exp(it) + math.exp(that))
  }

  extension (x: Logarithm)
    def toDouble = math.exp(x)
    def + (y: Logarithm) = Logarithm(math.exp(x) + math.exp(y))
    def * (y: Logarithm) = x + y


  @main def part3Ex(): Unit ={


    lazy val l: Logarithm = Logarithm(1.0)
    lazy val l2: Logarithm = Logarithm(2.0)

    println(l)
    println(l2)


  }
}