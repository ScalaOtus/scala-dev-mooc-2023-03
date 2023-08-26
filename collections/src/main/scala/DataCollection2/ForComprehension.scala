package DataCollection1

object ForComprehansion{
  def main(args: Array[String]): Unit = {
    val RGB = Seq("R", "G", "B")
    val range = Range(1,4)
    val map = Map("R"-> "Red", "G"->"Green", "B" -> "Blue")

    for (el <- RGB){
      println(el)
    }

    for(el <- RGB; el1 <- range){
      println(s"$el - $el1")
    }

    for ((key, value) <- map){
      println(s"$key - $value")
    }

    for (
      el1 <- RGB;
      el2 <- RGB;
      el3 <- RGB
    ){
      println(s"$el1 $el2 $el3")
    }

    println("make it uniq")
    for (
      el1 <- RGB;
      el2 <- RGB;
      el3 <- RGB;
      if el1 != el2;
      if el3 != el2 && el3 != el1
    ){
      println(s"$el1 $el2 $el3")
    }

    case class Car(marke: String, model: String, year: Int)
    val cars = Car("VW", "Passat", 2005) :: Car("Lexus", "UX", 2019) :: Car("BMW", "i3", 2021) :: Nil

    cars.filter(x => x.year > 2010).map(x=>x.model).foreach(x=>println(x))


    case class Garage(name: String, index:Int)
    val garages = Garage("BMW", 1) :: Garage("Lexus", 2) :: Nil

    println("get all cars related to collection garages")

    garages.flatMap{
      garage=>
        cars.filter(car => car.marke == garage.name).map(car => (car.marke, car.model))
    }.foreach(x=>println(s"${x._1} ${x._2}"))

    println("for Comprehension")
    val cars2010 = for {
      car <-cars if car.year > 2010
    } yield {
      (car.marke, car.model)
    }
    cars2010.foreach(x=>println(s"${x._1} ${x._2}"))

    println("for Comprehension1")

    val cars2010_1 = for{
      car <- cars
      garage <-garages
      if car.marke == garage.name
    } yield {
      (car.marke, car.model, garage.index)
    }

    cars2010_1.foreach{
      case (a,b,c) => println(s"$a $b $c")
    }
  }
}