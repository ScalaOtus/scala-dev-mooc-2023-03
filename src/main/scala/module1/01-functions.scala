package module1

object functions extends App {


  /**
   * Функции
   */


  /**
   * Реализовать ф-цию  sum, которая будет суммировать 2 целых числа и выдавать результат
   */



  
  // Partial function




  // SAM Single Abstract Method


  /**
   * Задание 1. Написать ф-цию метод isEven, которая будет вычислять является ли число четным
   */
  def isEven(num: Int): Boolean = num % 2 == 0

  println(isEven(3)) // false

  /**
   * Задание 2. Написать ф-цию метод isOdd, которая будет вычислять является ли число нечетным
   */
  def isOdd(num: Int): Boolean = num % 2 != 0

  println(isOdd(3)) // true

  /**
   * Задание 3. Написать ф-цию метод filterEven, которая получает на вход массив чисел и возвращает массив тех из них,
   * которые являются четными
   */
  def filterEven(lst: Iterable[Int]): Iterable[Int] = lst.filter(isEven)

  println(filterEven(1 to 10)) // Vector(2, 4, 6, 8, 10)


  /**
   * Задание 4. Написать ф-цию метод filterOdd, которая получает на вход массив чисел и возвращает массив тех из них,
   * которые являются нечетными
   */
  def filterOdd(lst: Iterable[Int]): Iterable[Int] = lst.filter(isOdd)

  println(filterOdd(1 to 10)) // Vector(1, 3, 5, 7, 9)


  /**
   * return statement
   *
   */
}