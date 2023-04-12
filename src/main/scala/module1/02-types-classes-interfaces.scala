package module1


import java.io.{Closeable, File}
import scala.io.{BufferedSource, Source}
import scala.util.{Try, Using}


object type_system {

  /**
   * Scala type system
   *
   */



  def absurd(v: Nothing) = ???


  // Generics


  lazy val file: File = ???

  lazy val source: BufferedSource = Source.fromFile(file)

//  lazy val lines: List[String] = source.getLines().toList

//  try {
//    source.getLines()
//  } finally {
//    source.close()
//  }

  def ensureClose[S <: Closeable, R](source : S)(f: S => R): R = {
    try {
      f(source)
    } finally {
      source.close()
    }
  }

//  val r: List[String] = ensureClose(source) { s =>
//    s.getLines().toList
//    s.getLines().toList
//    s.getLines().toList
//
//  }


  // ограничения связанные с дженериками


  /**
   *
   * class
   *
   * конструкторы / поля / методы / компаньоны
   */


  class User(val name: String = "Bob", val age: Int = 18){


    def printName(): Unit = println(name)
  }

  val user = User("", 20)
  user.printName()

  object User{
    def apply(name: String, age: Int): User = {
        new User(name, age)
    }
  }

  class Rectangle(val length: Int, val width: Int) {
    def p: Int = 2 * (length + width)
    def square: Int = length * width
  }



  /**
   * Задание 1: Создать класс "Прямоугольник"(Rectangle),
   * мы должны иметь возможность создавать прямоугольник с заданной
   * длиной(length) и шириной(width), а также вычислять его периметр и площадь
   *
   */


  /**
   * object
   *
   * 1. Паттерн одиночка
   * 2. Ленивая инициализация
   * 3. Могут быть компаньоны
   */


  /**
   * case class
   *
   */


  case class User2(name: String, age: Int)

  val user2 = User2("Tom", 10)
  val user4 = User2("Tom", 10)

  val user3 = user2.copy("Bob")


    // создать case класс кредитная карта с двумя полями номер и cvc


  /**
   * case object
   *
   * Используются для создания перечислений или же в качестве сообщений для Акторов
   */


  /**
   * trait
   *
   */

  trait WithId{
    def id: Int
  }

  sealed trait UserService{
    def getUser(id: Int): User
  }

  class UserServiceImpl extends UserService{
    override def getUser(id: Int): User = ???
  }

  val ui = new UserServiceImpl with WithId {
    override def id: Int = ???
  }
  val ui2: UserService = new UserService {
    override def getUser(id: Int): User = ???
  }



  class A {
    def foo() = "A"
  }

  trait B extends A {
    override def foo() = "B" + super.foo()
  }

  trait C extends B {
    override def foo() = "C" + super.foo()
  }

  trait D extends A {
    override def foo() = "D" + super.foo()
  }

  trait E extends C {
    override def foo(): String = "E" + super.foo()
  }

  // A -> D -> B -> C
  // CBDA
  val v = new A with D with C with B

  // A -> B -> C -> E -> D
  //
  val v1 = new A with E with D with C with B


  /**
   * Value classes и Universal traits
   */


}