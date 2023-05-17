package scala2.extendingclasses
/*
trait Show[A] {
  def show(a:A): String
}

object Show {
  def apply[A](implicit  instance: Show[A]): Show[A] = instance

  given Show[Int] with {
    def show(a:Int): String = a.toString
  }

  given List[A](using showA: Show[A]): Show[List[A]] with{
    def show(a: List[A]): String = ???
  }
}

object Main extends App {
  def printShow[A](a:A)(using Show[A]): Unit = {???}

  printShow(42)
  printShow("zstr")
  printShow(List(...))
}


def identity[A](a:A): A =a


def combine[A](a1: A, a2: A)(using ev: A => Numeric[A]): A
*/