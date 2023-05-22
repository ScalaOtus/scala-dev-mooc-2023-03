package catstypes
import cats.data.{Chain, Ior, Kleisli, NonEmptyChain, NonEmptyList, OptionT, Validated, ValidatedNec}
import cats.Monoid
import cats.implicits._
import cats.kernel.instances.int._
import cats.kernel.instances.string._

import scala.concurrent.Future
import scala.util.Try

object dataStructure{
  //Chain
  // конструкторы
  val empty: Chain[Int] = Chain[Int]()
  val empty2: Chain[Int] = Chain.empty[Int]
  val ch2 = Chain(1)
  val ch3 = Chain.one(1)
  val ch4: Chain[Int] = Chain.fromSeq(1::2::3::Nil)

  //операторы
  val ch5 = ch2 :+ 2 // append, const time
  val ch6 = 3 +: ch2 // prepend, const time
  val r = ch2.headOption // только Option, потому что тут не должно быть side эффектов

  ch3.map(_ +1)
  ch3.flatMap(v=>Chain.one(v+1))

  //NonEmptyChain
  val nec: NonEmptyChain[Int] = NonEmptyChain(1)
  val nec2: NonEmptyChain[Int] = NonEmptyChain.one(1)
  val nec3: Option[NonEmptyChain[Int]] = NonEmptyChain.fromSeq(1::2::3::Nil)
  //потому что List не совсем функционален

  val r2: Int = nec.head

  //NonEmptyList
  val nel1: NonEmptyList[Int] = NonEmptyList(1, List())
  val nel2: NonEmptyList[Int] = NonEmptyList.one(1)
  val nel3: Option[NonEmptyList[Int]] = NonEmptyList.fromList(1::Nil)
  //константное время только у head

  val n3: NonEmptyChain[Int] =nec3.head

}

object validation{
  type EmailValidationError = String
  type NameValidationError = String
  type AgeValidationError = String

  type Name = String
  type Email = String
  type Age = Int

  case class UserDTO(email:String, name: String, age:Int)
  case class User(email:String, name: String, age:Int)


  def emailValidatorE: Either[EmailValidationError, Email] = Left("Not valid Email")
  def usernameValidatorE: Either[NameValidationError, Name] = Left("Not valid Name")
  def ageValidationErrorE:Either[AgeValidationError, Age] = Right(30)

  //проблема - короткое замыкание
  def validateUserDataE(userDto: UserDTO):Either[String, User] = for{
    email <- emailValidatorE
    name <- usernameValidatorE
    age <- ageValidationErrorE
  } yield User(email, name, age)

  //Validated
  val v1  = Validated.valid[String, String]("foo")
  val v2 = Validated.invalid[String, String]("foo")

  val v3: Validated[Email, Email] = Validated.valid("foo")
  val v4: Validated[Email, Email] = Validated.invalid("foo")

  // конструкторы готовые валидаторы
  def emailValidatorV: Validated[EmailValidationError, Email] =
    "Email not valid".invalid[String]
  def usernameValidatorV: Validated[NameValidationError, Name] =
    "User name not valid".invalid[String]
  def ageValidationErrorV: Validated[String, Age] = 30.valid[String]

  //операторы
  // v1.map() //к успешному
  // v1.bimap(e =>, v =>) // в обе стороны
  // v1 combine v2

  //решение задачи по аналогии

  /// не компилируется!!!!!!!!!!!!!!!!
  /// когда не работает for comprehansion? когда нет map flatmap
  // тут их нет, Validated НЕ МОНАДА
  //  def  validateUserDataV(userDTO: UserDTO): Validated[String, User] = for {
  //    email <- emailValidatorV
  //    name <- usernameValidatorV
  //    age <- ageValidationErrorV
  //  } yield User(email, name, age)


  // метод combine
  def  validateUserDataV2(userDTO: UserDTO): Validated[String, String] =
    emailValidatorV combine usernameValidatorV combine ageValidationErrorV.map(_.toString)

  //можем сделать красивее, кортеж из 3х валидаторов и вызвать mapn
  //в качестве ошбки подходит Chain так как там накопление ошибок appen-ом
  // но надо расширить валидоторы implicit toValidatedNec
  // почему nonemptychain? потому что если ошибка произойдет она минимум 1 будет
  //обратите внимание, все работает из коробки, Validated сам собирает ошибки
  //для очень широкого спектра типов. показать на примере combine
  // он дженерик и может работать, если есть полугруппы типов ошибки и результата
  // тоетсь где то в cats implicit есть реалтзация semigroup для строки
  // тоесть если у нас свой case class ошибки, то это ищ коробки бы не заработало
  // но при реализации semigroup все бы снова скомпилировалось бы
  // при этом это можно сделать в обьекте компаньене
  def  validateUserDataV3(userDTO: UserDTO): ValidatedNec[String, User] = (
    emailValidatorV.toValidatedNec,
    usernameValidatorV.toValidatedNec,
    ageValidationErrorV.toValidatedNec
    ).mapN{
    //здесь есть лямбда, хдесь бес хаков toString
    (email, name, age) =>
      User(email, name, age)
  }

  //  def main(args: Array[String]): Unit = {
  //    println(validateUserDataV3(UserDTO("","",30)))
  //  }

  val u: User = User("a","b",30)

  lazy val ior: Ior[String, User] = Ior.Left("")
  lazy val ior1: Ior[String, User] = Ior.right(u)
  lazy val ior2: Ior[String, User] = Ior.both("warning", u)

  //операторы

  // задача
  def emailValidatorI = Ior.both("email ???", "sdf@dfg.de")
  def usernameValidatorI = Ior.both("name ???", "Musterman")
  def userageValidationI = 30.rightIor[String]
  //Ior является монадой, можно использовать for comprehansion
  def validateUserDataI(userDTO: UserDTO): Ior[String, User] = for {
    email <- emailValidatorI
    name <- usernameValidatorI
    age <- userageValidationI
  } yield  User(email, name, age)

  def validateUserDataI2(userDTO: UserDTO): Ior[NonEmptyChain[String], User] = for {
    email <- emailValidatorI.toIorNec
    name <- usernameValidatorI.toIorNec
    age <- userageValidationI.toIorNec
  } yield  User(email, name, age)

  def main(args: Array[String]): Unit = {
    println(validateUserDataI(UserDTO("","",30)))
    println(validateUserDataI2(UserDTO("","",30)))
  }

  // Kleisli

  val f1: Int => String = i => i.toString
  val f2: String => String = s => s + "dfg"
  val f3: Int => String = f1 andThen f2

  val f4: String => Option[Int] = _.toIntOption
  val f5: Int => Option[Int] = i => Try(10/i).toOption

  // давайте сделаем f6 как композицию f4 f5
  // интуитивно это f4 andthen f5 но не скомпилируется
  // разные типы данных int option[int]
  // для этого kleisli И существует
  val f6: Kleisli[Option, String, Int] = Kleisli(f4) andThen Kleisli(f5)
  //и из этого Kleisli мы можем получить функцию как результат вызова run
  val _f6 = f6.run //вот это уже композиция f4 f5
  // мы получили возможность композиции функций, возвращающих эффект
  // широко будет использоваться в http4s так как является клеем
  //для http route
  // hрttp4s это Kleisli над функцией request в result
  // _f6('') None _f6(0) None _f6(2) 5


  object  transformers{
    val f1: Future[Int] = Future.successful(2)
    def f2(i:Int): Future[Option[Int]] = Future.successful(Try(10/i).toOption)
    def f3(i:Int): Future[Option[Int]] = Future.successful(Try(10/i).toOption)

    import scala.concurrent.ExecutionContext.Implicits.global

    //хотим вычислисть f2 f3 передав туда результат future f1
    // val r = for{
    //   i1 <- f1    //int
    //   i2 <- f2(i1) //option[int]
    //   i3 <- f3(i1)//option[int]
    // } yield i2+i3  не компилируется, так как не совпадают типы
    //здесь может помочь monad transformer для контейнера который лежит в f
    //тоесть если в терминах future и внутри есть option в качестве f будет
    // future а monad transformer нужен для option
    val r: OptionT[Future, Int] = for{
      i1 <- OptionT.liftF(f1)    //конструктор, который сразу заворачивает в Option
      i2 <- OptionT(f2(i1)) //int теперь
      i3 <- OptionT(f3(i1)) //int теперь
    } yield i2+i3

    //тоесть мы не избавляемся от option, жто возможно только с помощью get getorelse
    //мы его маскируем для for comprehansion
    // и именно  OptionT берет на себя семантику короткого замыкания

    val _: Future[Option[Int]] = r.value

  }


  object cats_type_classes{
    // semigroup
    //обычный type конструктор с одним методом
    trait Semigroup[A]{
      // ассоциативнпя бинарная операция
      // combine(x, combine(y, z)) == combine(combine(x,y), z) Это закон
      // если деоать так, то все классы cats опирающиеся на typeclass semigroup
      // например validated, то мы можем быть уверены в корректности работы
      //
      def combine(x: A, y: A):A
    }

    object Semigroup{
      def apply[A](implicit ev:Semigroup[A]) = ev

      implicit val intSemigroup: Semigroup[Age] = new Semigroup[Int]{
        override def combine(x: Int, y: Int): Int = x+y
      }
    }
    // c помощью полугруппы можем комбинировать элементы
    val _ = (1::2::3::Nil).foldLeft(0)(Semigroup[Int].combine(_,_))
    //или со стандартной semigroup Из cats
    // |+| это вызов combine из cats на полугруппе
    val _ = (1::2::3::Nil).foldLeft(0)(_ |+| _)

    //Map("a" -> 1,"b" -> 2)
    //Map("b" -> 3,"c" -> 4)
    //Map("a" -> 1,"b" -> 5,"c" -> 4)  это то, что хотим получить при мердже


    //нужуе метод, который умеет делать сложение с option
    // на мапе по ключу будем спрашивать значение и будем получать
    // Option
    def optCombine[V:Semigroup](v: V, optV: Option[V]): V =
      optV.map(Semigroup[V].combine(v,_)).getOrElse(v)


    //для V нужен semigroup чтобы знать как складывать
    def mergeMap[K,V: Semigroup](lhs:Map[K,V], rhs:Map[K,V]): Map[K,V] =
      lhs.foldLeft(rhs){
        case (acc, (k, v)) =>
          acc.updated(k, optCombine(v,acc.get(k)))
      }

    //    }
  }
  //functor

  trait Functor[F[_]]{
    def map[A,B](fa: F[A])(f: A=>B): F[B]
  }

  // благодаря функторам можно маппиться на любых типах,
  // так как для любого tepe constructor можно реалтзовать
  // применение функции высщего порядка

  def id[A](e:A): A = e
  //законы
  // map id map(id)==a
  // map function composition
  // есть 2 функции f и g
  // f andThen g => map(f andThen g) == map(f) andThen map(g)
  //мэп от композиции равен композиции

}
