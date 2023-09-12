package DataCollection1

object DataCollections {
  def main(args: Array[String]): Unit ={
/*    // 1
    val demoCollectionList = "line 1" :: "line 2" :: "line 3" :: Nil
    // 2
    val demoCollectionSet = ("line 1" :: "line 1" :: "line 2" :: "line 3" :: Nil).toSet

    val demoCollectionSet1 = ("line 1" :: "line 1" :: "line 2" :: "line 3" :: Nil).groupBy(x=>x).map(x=>x._1)
//    demoCollectionSet.foreach(x=> println(x))

    //3 iteration
    val iter = demoCollectionList.iterator

/*    while (iter.hasNext){
      println(iter.next)
    }
    */

    println("demo functions")
    val demoCollection = 1 :: 2 :: 3 :: Nil
    println(s"fold result: ${demoCollection.fold(0)((z, i) => z+i)}")
    println(s"fold left result: ${demoCollection.foldLeft(0)((z, i) => z+i)}")
    println(s"reduce result: ${demoCollection.reduce((z, i) => z+i)}")


    val test = List(1,2,3,4,5) :: List(1,50,3) :: List(1,2) :: Nil
    test.filter(x=> x.reduce((y,z)=> y+z) > 10).foreach(x=>println(x.mkString(",")))
*/
    val testFlatMap = List(List(1,2,3), List(4,5,6), List(7,8,9))
    // we want to get => List(1,2,3,4,5,6,7,8,9)
    println(testFlatMap.flatMap(x=>x).mkString(","))



  }

}

