package scala3_1


object scala3macros {
  trait Show[T]{
    inline def show(x:T):String
  }

  case class  Foo(x:Int)

  inline  given Show[Foo] with {
    inline def show(x:Foo): String  = s"11 ${x.toString} 111"
  }


  @main def scala3macrosEx()={
    println(summon[Show[Foo]].show(Foo(67)))

  }

}


/*
transparent inline  def default(inline  name: String): Any =
  inline  if name == "Int" then 0
  else inline if name ="String" then ""
  else ...

val n0:Int = default("Int")
val n2:String = default("String")
*/