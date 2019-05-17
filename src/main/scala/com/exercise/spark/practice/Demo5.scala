package com.exercise.spark.practice

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SparkSession}

//某市公安局要对全市的安检进行数字化布防，假设你是其中的一名干警，根据领导要求要对全市的旅馆进行数据分析，现在你有以下数据：
//表A:重点监控人员的身份证号名单。(ID String)
//表B:旅馆的入住人员身份证号和入店出店时间。(ID String, INTIME String(yyyy-MM-dd HH:mm:ss), OUTTIME String(yyyy-MM-dd HH:mm:ss))
//试分析：
//1、全部旅客的两次之间的住店间隔，即每个旅客每隔多长时间住店一次（以天为单位，第一次无间隔，记为0）。
//2、全部旅客的每次住店时常，即每个旅客每次住店多长时间（小时）。
//3、全部旅客的每次入店时间和出店时间（以小时为单位，四舍五入），
//    比如李华2018-08-15 12:01:00入店，则入店时间记为12，2018-08-15 21:49:00出店，则记为22。
//4、正常旅客和重点人员的的住店间隔、住店时常、入店时间和出店时间的统计：
//    众数，中位数，平均值，最大值，最小值，每个时间段的计数（如：住店间隔为2天的为30次，住店时常为8小时的为60次）。

//数据示例
//tableA
//{"ID": "daae2049-31ea-42f3-82d4-167db8a32f52"}
//{"ID": "ef73789f-c790-47a7-beac-de161cd908ff"}
//tableB
//{"ID": "9c05ff3b-7582-46d5-acd5-5b788ca2209e", "INTIME": "2016-03-16 19:34:13", "OUTTIME": "2016-03-17 00:34:13"}
//{"ID": "c975ba39-9cf1-4c81-915c-3790ccad5a84", "INTIME": "0115-01-23 21:49:04", "OUTTIME": "2015-01-24 03:49:04"}

object Demo5 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sc: SparkContext = new SparkContext(sparkConf)
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    val df1: DataFrame = spark.read.json("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\demo5.tableA.json")
    val df2: DataFrame = spark.read.json("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\demo5.tableB.json")

//------------------------------------------------------------------
    //全部旅客的两次之间的住店间隔，即每个旅客每隔多长时间住店一次（以天为单位，第一次无间隔，记为0）。
    //主要问题是如何令每个顾客的入住时间相减？用for循环
    //思路：





  }

}
