package module1

object pattern_matching{
     // Pattern matching

  /**
   * Матчинг на типы
   */

   val i: Any = ???

   i match {
     case v: Int => "int"
     case v: String => "string"
     case v: List[Int] => "list int"
     case v: List[String] => "list str"
     case _ => "???"
   }

  /**
   * Структурный матчинг
   */

  // isInstanceOf
  // asInstanceOf

  val Dog(n, age) = Dog("a", 10)

  sealed trait Animal{
    def name: String
    def age: Int

    def whoIam: String  = this match {
      case Dog(n, a) => s"i'm dog $n"
      case Cat(n, a) => s"i'm cat $n"
    }
  }


  case class Dog(name: String, age: Int) extends Animal


  case class Cat(name: String, age: Int) extends Animal

  /**
   * Матчинг на литерал
   */

  val dog: Animal = ???

  val Bim = "Bim"

  dog match {
    case Dog(Bim, age) =>
    case Cat(name, age) =>
    case _ =>
  }


  /**
   * Матчинг на константу
   */


  /**
   * Матчинг с условием (гарды)
   */

  dog match {
    case Dog(n, age) if n == "Bim" =>
    case Cat(name, age) =>
    case _ =>
  }


  /**
   * "as" паттерн
   */

  def treatCat(cat: Cat) = ???
  def treatDog(dog: Dog) = ???

  def treat(a: Animal): Animal = a match {
    case d @ Dog(n, a) =>
      treatDog(d)
    case c @ Cat(_, _) =>  treatCat(c)
  }


  /**
   * используя паттерн матчинг напечатать имя и возраст
   */



  final case class Employee(name: String, address: Address)
  final case class Address(street: String, number: Int)

  val alex = Employee("Alex", Address("XXX", 221))

  /**
   * воспользовавшись паттерн матчингом напечатать номер из поля адрес
   */

  alex match {
    case Employee(_, Address(_, number)) => println(number)
  }



  /**
   * Паттерн матчинг может содержать литералы.
   * Реализовать паттерн матчинг на alex с двумя кейсами.
   * 1. Имя должно соотвествовать Alex
   * 2. Все остальные
   */




  /**
   * Паттерны могут содержать условия. В этом случае case сработает,
   * если и паттерн совпал и условие true.
   * Условия в паттерн матчинге называются гардами.
   */



  /**
   * Реализовать паттерн матчинг на alex с двумя кейсами.
   * 1. Имя должно начинаться с A
   * 2. Все остальные
   */


  /**
   *
   * Мы можем поместить кусок паттерна в переменную использую `as` паттерн,
   * x @ ..., где x это любая переменная.
   * Это переменная может использоваться, как в условии,
   * так и внутри кейса
   */

    trait PaymentMethod
    case object Card extends PaymentMethod
    case object WireTransfer extends PaymentMethod
    case object Cash extends PaymentMethod

    case class Order(paymentMethod: PaymentMethod)

    lazy val order: Order = ???

    lazy val pm: PaymentMethod = ???


    def checkByCard(o: Order) = ???

    def checkOther(o: Order) = ???



  /**
   * Мы можем использовать вертикальную черту `|` для матчинга на альтернативы
   */

   sealed trait A
   case class B(v: Int) extends A
   case class C(v: Int) extends A
   case class D(v: Int) extends A

   val a: A = ???

}