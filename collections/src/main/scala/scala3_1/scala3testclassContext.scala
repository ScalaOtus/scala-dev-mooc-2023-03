package scala

class GivenInt(using val usingParam: Int)(using val usingParam1: String) {
  def myInt = summon[Int](using usingParam)
  def myString = summon[String](using usingParam1)
}

object scala3testclassContext {
  @main def scala3testclassContextEx()={
    val b = GivenInt(using 42)(using "test")
    import b.given

    println(usingParam)
    println(usingParam1)

    println(summon[Int])
    println(summon[String])


  }

}