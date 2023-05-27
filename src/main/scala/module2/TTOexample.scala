package module2


/** Расширить возможности типа String, методом trimToOption, который возвращает Option[String]
 * если строка пустая или null, то None
 * если нет, то Some от строки со всеми удаленными начальными и конечными пробелами
 */


object Utils {

  implicit class StringHelper(in: String){


    //вот этот вариант видел:  Option(str).map(_.trim).filter(_.nonEmpty)
    //сначала из головы написал, а потом уже сравнил,
    //редко фильтр на Option использую, поэтому вот так написал = (
    implicit def trimToOption: Option[String] = {
      if (in == null || in.isEmpty) None
      else Some(in.trim)
    }
  }

  def main(args: Array[String]): Unit = {
    val catName = "   The Greate Foxy   !   "
    assert(catName.trimToOption == Option("The Greate Foxy   !"))
    val x: String = null
    assert(x.trimToOption.isEmpty)
    assert("".trimToOption.isEmpty)
  }

}
