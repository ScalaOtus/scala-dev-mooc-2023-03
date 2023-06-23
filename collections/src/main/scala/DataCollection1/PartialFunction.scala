package DataCollection1

object PartialFunction{
  def main(args: Array[String]) : Unit= {
    val func = new PartialFunction[Double, Double] {
      override def isDefinedAt(x: Double): Boolean = x > 0
      override def apply(x: Double): Double = x * 2
    }


    val data  = 1:: 3::5::"test"::6::Nil

    val res = data.collect{
      case x: String => s"test $x"
      case x: Int => x*2
    }

    //customabs
    val funcAbsOneSide = new PartialFunction[Int, Int]{
      override def isDefinedAt(x: Int): Boolean = x < 0
      override def apply(x: Int): Int = x * -1
    }

    val funcAbsOtherSide = new PartialFunction[Int, Int]{
      override def isDefinedAt(x: Int): Boolean = x >= 0
      override def apply(x: Int): Int = x
    }

    val newAbs: PartialFunction[Int, Int] = {
      funcAbsOneSide orElse  funcAbsOtherSide
    }

    println(newAbs(-5))
    println(newAbs(5))


  }

}