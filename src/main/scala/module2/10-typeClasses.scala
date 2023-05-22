package module2

import module2.type_classes.JsValue.{JsNull, JsNumber, JsString}


object type_classes {

  sealed trait JsValue
  object JsValue {
    final case class JsObject(get: Map[String, JsValue]) extends JsValue
    final case class JsString(get: String) extends JsValue
    final case class JsNumber(get: Double) extends JsValue
    final case object JsNull extends JsValue
  }


  // 1

  trait JsonWriter[T]{
    def write(v: T): JsValue
  }

  object JsonWriter{

    def from[T](f: T => JsValue): JsonWriter[T] = new JsonWriter[T] {
      override def write(v: T): JsValue = f(v)
    }

    // 2
    implicit val intJsWriter = from[Int](JsNumber(_))

    implicit val strJsWriter = from[String](JsString)

    implicit def optToJsValue[A](implicit ev: JsonWriter[A]): JsonWriter[Option[A]] =
      from[Option[A]] {
        case Some(value) => ev.write(value)
        case None => JsNull
      }

    // summoner
    def apply[T](implicit ev: JsonWriter[T]): JsonWriter[T] = ev
  }

  // 4
  implicit class JsonSyntax[T](v: T){
    def toJson(implicit ev: JsonWriter[T]): JsValue = ev.write(v)
  }


  // 3
  def toJson[T: JsonWriter](v: T): JsValue = JsonWriter[T].write(v)

  toJson(10)
  toJson("abc")
  toJson[Option[Int]](Some(10))
  toJson[Option[String]](Some("abc"))

  10.toJson
  "abc".toJson
  Option(10).toJson



  // 1
  trait Ordering[T]{
    def less(a: T, b: T): Boolean
  }



  object Ordering{

    def from[A](f: (A, A) => Boolean): Ordering[A] = new Ordering[A] {
      override def less(a: A, b: A): Boolean = f(a, b)
    }

    // 2
    implicit val intOrdering = Ordering.from[Int]((a, b) => a < b)

    implicit val strOrdering = Ordering.from[String]((a, b) => a < b)
  }


  // 3
  def _max[A](a: A, b: A)(implicit ev: Ordering[A]): A =
    if(ev.less(a, b)) b else a

  _max(10, 20)
  _max("ab", "bcd")


  // 1
  trait Eq[T]{
    def ===(a: T, b: T): Boolean
  }

  // 2
  object Eq{

    def apply[T](implicit eq: Eq[T]) = eq

    implicit val eqStr: Eq[String] = new Eq[String] {
      override def ===(a: String, b: String): Boolean = a == b
    }
  }


  implicit class EqSyntax[T: Eq](a: T){
    // 3
    def ===(b: T): Boolean =
      Eq[T].===(a, b)
  }


  val result = List("a", "b", "c").filter(str => str === "")

}
