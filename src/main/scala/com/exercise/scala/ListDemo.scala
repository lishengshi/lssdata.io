package com.exercise.scala

import scala.collection.mutable

object ListDemo {
  def main(args: Array[String]): Unit = {
    val list0 = List(1,2,3,4,5,6,7,8)
    val list1 = list0.map(_*2)
    val a: Int => Boolean = (d:Int) => d%2==0
    val list2: List[Int] = list0.filter(a)
    val list3: List[Int] = list0.sorted
    val list4: List[Int] = list3.reverse
    val it: Iterator[List[Int]] = list0.grouped(2)

    val buffer: mutable.Buffer[List[Int]] = it.toBuffer
    val list5: List[List[Int]] = it.toList
    val list7: List[Int] = list5.flatten
    buffer.foreach(println(_))
    val res2: Int = list0.reduce(_+_)



  }



}
