

object Main {

  def main(args: Array[String]): Unit = {


    def sumItUp: Int = {
      def one(x: Int): Int = { return x; 1 }
      val two = (x: Int) => { return x; 2 }
      1 + one(2) + two(5)
    }

    println(sumItUp)
    println("Hello world")
  }
}