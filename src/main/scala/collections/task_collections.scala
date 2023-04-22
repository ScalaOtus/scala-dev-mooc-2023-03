package collections

object task_collections extends App {

  def isASCIIString(str: String): Boolean = str.matches("[A-Za-z]+")

  /**
   * Реализуйте метод который первый элемент списка не изменяет, а для последующих алгоритм следующий:
   * если isASCIIString is TRUE тогда пусть каждый элемент строки будет в ВЕРХНЕМ регистре
   * если isASCIIString is FALSE тогда пусть каждый элемент строки будет в нижнем регистре
   * Пример:
   * capitalizeIgnoringASCII(List("Lorem", "ipsum" ,"dolor", "sit", "amet")) -> List("Lorem", "IPSUM", "DOLOR", "SIT", "AMET")
   * capitalizeIgnoringASCII(List("Оказывается", ",", "ЗвУк", "КЛАВИШЬ", "печатной", "Машинки", "не", "СТАЛ", "ограничивающим", "фактором")) ->
   * List("Оказывается", "," "звук", "КЛАВИШЬ", "печатной", "машинки", "не", "стал", "ограничивающим", "фактором")
   * HINT: Тут удобно использовать collect и zipWithIndex
   *
   * * */
  def capitalizeIgnoringASCII(text: List[String]): List[String] = text
    .zipWithIndex
    .map {
      case (word, index) if index == 0 => word
      case (word, _) if isASCIIString(word) => word.toUpperCase
      case (word, _) => word.toLowerCase
    }

  assert(capitalizeIgnoringASCII(List("Lorem", "ipsum", "dolor", "sit", "amet")) ==
    List("Lorem", "IPSUM", "DOLOR", "SIT", "AMET"))
  assert(capitalizeIgnoringASCII(List("Оказывается", ",", "ЗвУк", "КЛАВИШЬ", "печатной", "Машинки", "не", "СТАЛ", "ограничивающим", "фактором")) ==
    List("Оказывается", ",", "звук", "клавишь", "печатной", "машинки", "не", "стал", "ограничивающим", "фактором"))



  /**
   *
   * Компьютер сгенерировал текст используя вместо прописных чисел, числа в виде цифр, помогите компьютеру заменить цифры на числа
   * В тексте встречаются числа от 0 до 9
   *
   * Реализуйте метод который цифровые значения в строке заменяет на числа: 1 -> one, 2 -> two
   *
   * HINT: Для всех возможных комбинаций чисел стоит использовать Map
   * **/
  def numbersToNumericString(text: String): String = text
    .split(" ")
    .map {
      case "0" => "zero"
      case "1" => "one"
      case "2" => "two"
      case "3" => "three"
      case "4" => "four"
      case "5" => "five"
      case "6" => "six"
      case "7" => "seven"
      case "8" => "eight"
      case "9" => "nine"
      case word => word
    }
    .toList
    .foldLeft("")((acc,word) => acc + " " + word)
    .strip()

  assert(numbersToNumericString("Lorem ipsum 1 dolor 9 sit 0 amet") == "Lorem ipsum one dolor nine sit zero amet")
  assert(numbersToNumericString("Lorem 3 ipsum 4 dolor 7 sit 8 amet 0") == "Lorem three ipsum four dolor seven sit eight amet zero")

  /**
   *
   * У нас есть два дилера со списками машин которые они обслуживают и продают (case class Auto(mark: String, model: String)).
   * Базы данных дилеров содержат тысячи и больше записей. Нет гарантии что записи уникальные и не имеют повторений
   * HINT: Set
   * HINT2: Iterable стоит изменить
   * **/

  case class Auto(mark: String, model: String)

  /**
   * Хотим узнать какие машины можно обслужить учитывая этих двух дилеров
   * Реализуйте метод который примет две коллекции (два источника) и вернёт объединенный список уникальный значений
   **/
  def intersectionAuto(dealerOne: Iterable[Auto], dealerTwo: Iterable[Auto]): Iterable[Auto] =
    dealerOne.toSet union dealerTwo.toSet


  /**
   * Хотим узнать какие машины обслуживается в первом дилеромском центре, но не обслуживаются во втором
   * Реализуйте метод который примет две коллекции (два источника)
   * и вернёт уникальный список машин обслуживающихся в первом дилерском центре и не обслуживающимся во втором
   **/
  def filterAllLeftDealerAutoWithoutRight(dealerOne: Iterable[Auto], dealerTwo: Iterable[Auto]): Iterable[Auto] =
    dealerOne.toSet diff dealerTwo.toSet

  val dealerOne: Iterable[Auto] = List(
    Auto("Ford", "T"),
    Auto("Ford", "T"),
    Auto("Ford", "Mustang"),
    Auto("Ford", "Mustang"),
    Auto("Ford", "GT"),
    Auto("Ford", "GT"),
    Auto("Ford", "1")
  )

  val dealerTwo: Iterable[Auto] = List(
    Auto("Ford", "T"),
    Auto("Ford", "T"),
    Auto("Ford", "GT"),
    Auto("Ford", "GT"),
    Auto("Ford", "1"),
    Auto("Ford", "S")
  )

  assert (intersectionAuto(dealerOne, dealerTwo) == Set(Auto("Ford","T"), Auto("Ford","Mustang"), Auto("Ford","GT"), Auto("Ford","1"),  Auto("Ford", "S")))
  assert (filterAllLeftDealerAutoWithoutRight(dealerOne, dealerTwo) == Set(Auto("Ford", "Mustang")))
}