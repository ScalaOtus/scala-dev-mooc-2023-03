package homework.hw7

object hw7 {
  object homework1 {

    extension (x: String)
    def +++(y: String): Int =
      x.toInt + y.toInt

    @main def part1Ex(): Unit ={
      println("56" +++ "3")
    }

  }


  object homework2 {
    enum CompletionArg {
      case StrCompletion(s: String)
      case IntCompletion(i: Int)
      case FloatCompletion(f: Float)
    }

    import CompletionArg.*

    object CompletionArg {
      given fromString: Conversion[String, CompletionArg] = StrCompletion(_)
      given fromInt: Conversion[Int, CompletionArg] = IntCompletion(_)
      given fromFloat: Conversion[Float, CompletionArg] = FloatCompletion(_)
    }


    @main def part2Ex(): Unit ={
      println("cat")
      println(Completions.complete("String"))
      println(Completions.complete(1))
      println(Completions.complete(7f))
    }

    object Completions {


      def complete[T](arg: CompletionArg): Any = arg match
      case StrCompletion(s) => s
      case IntCompletion(i) => i
      case FloatCompletion(f) => f
    }

  }

  object homework3 {

    opaque
    type Logarithm = Double

    object Logarithm {
      def apply(d: Double): Logarithm = math.log(d)

      def safe(d: Double): Option[Logarithm] =
        if d > 0.0 then Some(math.log(d)) else None
    }

    extension(x: Logarithm)

    def toDouble: Double = math.exp(x)

    def +(y: Logarithm): Logarithm = Logarithm(math.exp(x) + math.exp(y))

    def *(y: Logarithm): Logarithm = x + y

    @main def part3Ex(): Unit = {
      import Logarithm
      val l = Logarithm(1.0D)
      val l2 = Logarithm(2.0D)
      val l3 = l * l2
      val l4 = l + l2
    }

  }

}

