package com.exercise.util

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


object BaseUtil {

  lazy val spark: SparkSession = SparkSessionUtil.getSparkSession
  //隐式转换方法
  implicit def int2boolean(a:Int):Boolean={
    if (a==0) false else true
  }

  /**
    * 得到代码块的运行时间
    *
    * @param block 需要测试的代码块
    * @return Turple(代码块返回值 | 代码运行时间)
    */
  def getMethodRunTime[R](block: => R): (R, String) = {
    val start = System.nanoTime() //系统纳米时间
    val result = block
    val end = System.nanoTime()
    val delta = end - start
    val ms = delta / 1000000d //毫秒
    val s = ms / 1000d //秒
    val min = s / 60d //分钟
    (result, s.formatted("%.3f") + "s") //保留三位小数  单位 秒
    //(result, min.formatted("%.2f") + "min") //保留两位小数   单位 分钟
  }

  /**
    * 用于睡眠程序（默认10分钟），以查看spark UI
    */
  def sleepApp(ms: Long = 1000 * 60 * 10): Unit = {
    println("正在睡眠，持续10分钟..............")
    Thread.sleep(ms)
  }

  /**
    * RDD的装饰类（隐式转换）,不加泛型读取不到
    */
  class RichRDD(rdd:RDD[_<:Any]){
    /**
      * 每个元素在分区位置信息，不能打印太多元素
      */
    def printIterLoc():Unit={
      println("分区位置信息========================")
      rdd.mapPartitionsWithIndex{
        case (index,iter)=> Iterator(s"part_$index: ${iter.mkString(",")}")
      }.collect().foreach(println(_))
    }
    /**
      * 打印每个分区的元素数量
      */
    def printPartIterNum():Unit={
      println("每个分区的元素数量如下=============")
      rdd.mapPartitionsWithIndex{
        case (partIndex,iter)=>Iterator(("part_"+partIndex,iter.size))
      }.collect().foreach(println)
    }
  }

  /**
    * 扩展RDD的方法，隐式转换
    * 类似装饰模式：在不改变其结构基础上扩充某个类的方法
    * 装饰模式？TODO
    */
  implicit def rdd2RichRDD(src:RDD[_ <: Any]):RichRDD = new RichRDD(src)

}
