val i: Any = List("Hello")

i match {
  case v: Int => "int"
  case v: String => "string"
  case v: List[Int] => "list int"
  case v: List[String] => "list str"
  case _ => "???"
}
