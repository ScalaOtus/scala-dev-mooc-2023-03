import module1.{list, opt}

object Main {

  def main(args: Array[String]): Unit = {


    def sumItUp: Int = {
      def one(x: Int): Int = { return x; 1 }
      val two = (x: Int) => { return x; 2 }
      1 + one(2) + two(5)
    }

    println(sumItUp)
    println("Hello world")

    println("  Module 01 EX 01 ")

    println("=================Option=============")
    opt.printIfAny(opt.Option.Some("some"))
    opt.printIfAny(opt.Option.None)
    println(opt.zip(opt.Option.Some("a"), opt.Option.Some("b")))
    println(opt.filter(opt.Option.Some(5))(x => x < 10)) // 5
    println(opt.filter(opt.Option.Some(5))(x => x > 10)) // None
    println("=================List===============")
    println(list.mkString(list.List(111,222,3333,4444))("<"))
    println(list.::(55)(list.List(66,77,88)))
    println(list.::(55)(list.List.Nil))
    println(list.cons(1,5,6))
    println(s"Reverse - ${list.reverse(list.List(1,2,3,4), list.List.Nil)}")
    println(list.map(list.List(1,2,3,4))(x => x+2))
    println(list.filter(list.List(1,2,3,4))(x => x > 2))
    println(list.incList(list.List(1,2,3,4)))
    println(list.shoutString(list.List("help", "me", "finish", "this", "course")))



  }
}