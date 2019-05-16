package com.exercise.scala

object WordCount {
  def main(args: Array[String]): Unit = {
    val list = List("a b c","a a b")
    val flat: List[String] = list.flatMap(_.split(" "))
    val tuples: List[(String, Int)] = flat.map((_,1))
    val groupBy: Map[String, List[(String, Int)]] = tuples.groupBy(_._1)
    val mapvalue: Map[String, Int] = groupBy.mapValues(_.size)
    val sorted: List[(String, Int)] = mapvalue.toList.sortBy(_._2)
    println(sorted)
    val sortWith: List[(String, Int)] = mapvalue.toList.sortWith(_._2<_._2)
    println(sortWith)




  }

}
