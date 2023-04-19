package DataCollection2

object Monad {

  class Lazy[+A](v: => A) {

    private lazy val internal = v

    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internal)

    def map[B](f: A => B): Lazy[B] = flatMap(x => new Lazy(f(x)))
  }

  object Lazy {
    def apply[A](v: => A): Lazy[A] = new Lazy(v)
  }


  def main(args: Array[String]): Unit ={
    val res = for {
      el1 <- Lazy(1)
      el1 <- Lazy(2)
    } yield ()
  }
}