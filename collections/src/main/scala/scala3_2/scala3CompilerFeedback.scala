package scala2.extendingclasses


trait Order[A]{
  def compare(aq: A, a2: A):Int
}

object Order{
  implicit def orderList[A](implicit  orderA: Order[A]): Order[List[A]] = ???
}

def sort[A](list: List[A])(implicit order: Order[A]): List[A] = ???


class Foo

object Foo{
  implicit  def orderBar: Order[Foo] = ???

}

object testfeedback{
  sort(List(List(new Foo)))
}


class Bar

object Implicits{
  implicit  def orderBar: Order[Bar] = ???
}

object testfeedback1 {
  import scala2.extendingclasses.Implicits.orderBar
  sort(List(new Bar))
}