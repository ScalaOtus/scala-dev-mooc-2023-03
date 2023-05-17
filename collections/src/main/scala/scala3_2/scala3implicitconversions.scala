package scala3_2

object scala3implicitconversions {
  @main def scala3implicitconversionsEx() = {
    given int2Integer: Conversion[Int, java.lang.Integer] =
      java.lang.Integer.valueOf(_)

    val x: Integer = 6
    val x1 :Int = 2
    println(x1+x)


  }

}