package scala

//1. extension methods
object Extensionmethid {

  case class Circle(x: Double, y: Double, radius: Double)

  extension (c: Circle)
    def cirllength: Double = c.radius*math.Pi*2

  @main def ExtensionmethidEx(): Unit = {
    val circle = Circle(0,0,1)
    println(circle.cirllength)
  }
}

//2. Operators
object Operators {
  extension (x: String)
    def < (y: String): Boolean = x.length < y.length

  @main def OperatorsEx() ={
    println("a" < "aa")
  }
}

//3. Collective Extensions
object CollectiveExtension{
  extension  (ss: Seq[String])
    def longestStrings: Seq[String] =
      val maxLenght = ss.map(_.length).max
      ss.filter(_.length == maxLenght)

    def longestString: String = longestStrings.head

/*

  extension  (ss: Seq[String])
    def longestStrings: Seq[String] =
      ???

  extension  (ss: Seq[String])
    def longestString: Seq[String] =
      ...
*/


  @main def CollectiveExtensionEx() ={
    println(("a" :: "bb" :: "ccc" :: Nil).longestString )
  }
}


trait IntOps:
  extension  (i: Int) def isZero: Boolean = i == 0
  extension  (i: Int) def safeMod(x:Int): Option[Int] =
    if x.isZero then None else Some(i*x)

object IntOpsEx extends IntOps:
  extension  (i: Int) def safeDiv(x:Int): Option[Int] =
    if x.isZero then None
    else Some(i/x)

trait SafeDiv:
  import IntOpsEx.*

  extension (i:Int) def divide(d: Int): Option[(Int, Int)] =
    (i.safeDiv(d), i.safeMod(d)) match
      case (Some(d), Some(r)) => Some((d,r))
      case _ => None

object scala3extensions{
  given ops1: IntOps() with {}

  @main def scala3extensionsEx()={
    println(2.safeMod(2))

  }

}

