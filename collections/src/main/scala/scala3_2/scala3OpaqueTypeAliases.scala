package scala2.extendingclasses

object  MyMath{
  opaque type Logarithm = Double

  object Logarithm{

    def apply(d:Double): Logarithm = math.log(d)

    def safe(d:Double): Option[Logarithm] =
      if d>0 then Some(math.log(d)) else None
  }

  extension (x:Logarithm)
    def toDouble: Double  = math.exp(x)
    def + (y:Logarithm): Logarithm = Logarithm(math.exp(x) + math.exp(y))
    def * (y:Logarithm):Logarithm = x+y
}

object scala3OpaqueTypeAliases {
  @main def scala3OpaqueTypeAliasesEx()={
    import MyMath.Logarithm

    val l1 = Logarithm(1.0)
    val l2 = Logarithm(2.0)
    val l3 = l1 * l2
    val l4 = l1 + l2
  }

}
