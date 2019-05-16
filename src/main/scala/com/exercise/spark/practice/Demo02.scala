package com.exercise.spark.practice

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable


/*  现有userindo.txt，内有若干行用户数据，每一行代表一条用户数据包括两个字段，
    第一个字段为用户编号，第二个字段为用户事件触发时间，监控程序监控用户动作，监控开始后每一分钟生成一条记录，
    用户每间隔一分钟响应一次的话为持续响应，中间超过1分钟即断开，后续记录为第二次持续响应。
    1、统计用户的每个阶段连续响应时间，如果一个用户多次中断那么将产生多条统计数据；
    2、查找并输出每个用户的最长持续阶段的相应信息。
    说明：输出格式为用户编号,开始时间-结束时间,持续分钟数，如：
    A,2018-01-01 00:11:00-2018-01-01 00:18:00,7  */

object Demo02 {
  def main(args: Array[String]): Unit = {

    demo1()
  }
  //思路：读取数据，按照用户分组后生成时间的一个Iterate，然后进行排序，循环这个数组
  //后者减去前者，如果不为1min，那么就认为是中断，生成（use，时间，持续时间）的DataFrame
  def demo1():Unit={
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo02")
    val sc = new SparkContext(conf)
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    val rdd1: RDD[(String, Long)] = spark.sparkContext.textFile("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\userindo.txt")
      .map(line => line.split(",")).map(x => {
      val dateTime: Long = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(x(1)).getTime
      (x(0), dateTime)
    })

    //这儿需要 按照key分组后，整合values排序
    val resultRDD: RDD[(String, (Long, Long))] = rdd1.groupByKey().flatMapValues(value => {
      //      val sortList: List[Long] = value.toList.sortBy(x=>x)
      val sortList: List[Long] = value.toList.sorted
      val result = new mutable.HashSet[(Long, Long)]()
      val duration = 60 * 1000
      var start = sortList(0)
      var pre = sortList(0)
      for (x <- 0 until sortList.size) {
        if (sortList(x) - pre == duration) {
          //没有中断,指针下移
          pre = sortList(x)
        } else {
          //中断，返回 （end，start）
          result += ((start, pre))
          //重置start pre
          start = sortList(x)
          pre = sortList(x)
        }
      }
      //最后把没有中断的最后一个加上，(因为有可能只剩一个，所以有可能是相同的值，处理成间隔为0)
      result.+=((start, pre))
      result
    })
    import spark.implicits._
    resultRDD
    val resultDF: DataFrame = resultRDD.map(ele => {
      val startTime: Long = ele._2._1
      val endTime: Long = ele._2._2
      val duration = (endTime-startTime)/(60 * 1000)
      (ele._1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(startTime)) + "->" +
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(endTime)), duration)
    }).toDF()
    resultDF.show()
    spark.stop()
  }

}