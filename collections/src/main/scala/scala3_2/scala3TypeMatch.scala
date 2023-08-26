package scala3_2


object scala3TypeMatch {

  type FirstComponentOf[T] = T match
    case String => Option[Char]
    case Int => Int
    case Iterable[t] => Option[t]

  def firstComponentOf[U](elem: U): FirstComponentOf[U] = elem match
    case s: String => ???
    case s: Int => ???


  @main def scala3TypeMatchEx()={

    val aNumber: FirstComponentOf[Int] = 2
    val aChar: FirstComponentOf[String] = Some('b')

  }
}