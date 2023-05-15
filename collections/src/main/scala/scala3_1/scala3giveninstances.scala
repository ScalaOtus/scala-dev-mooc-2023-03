package scala3_1


object scala3giveninstances {
  trait Ord[T] {
    def compare(x: T, y: T): Int

    extension  (x:T)
      def <(y:T) = compare(x,y) < 0
      def >(y:T) = compare(x,y) > 0
  }

  given intOrd: Ord[Int] with {
    def compare(x:Int, y:Int) =
      if x < y then -1 else if x > y then 1 else 0
  }

  given listOrd[T](using ord: Ord[T]): Ord[List[T]] with{
    def compare(xs: List[T], ys: List[T]): Int = (xs, ys) match {
      case (Nil, Nil) => 0
      case (Nil, _ ) => -1
      case (_, Nil) => 1
      case (x :: xs1, y :: ys1) => {
        val fst = ord.compare(x,y)
        if fst != 0 then fst else compare(xs1, ys1)
      }
    }
  }

  def max[T](x:T,y:T)(using ord: Ord[T]): T ={
    if ord.compare(x, y) < 0 then y else x
  }
  
  def maximum[T](xs:List[T])(using Ord[T]): T =
    xs.reduceLeft(max)

  @main def scala3givenInstancesEx() ={
    val x = 1 :: 2 :: 3 :: 4 :: Nil
    val y = 1 :: 2 :: 3 :: 7 :: Nil

    println(max(y,x))
    println(max(5,6))

  }



}