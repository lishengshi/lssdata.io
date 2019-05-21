package com.exercise.spark.practice

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object Demo7 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sc: SparkContext = new SparkContext(sparkConf)
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    val df1: DataFrame = spark.read.json("D:\\t1.json")
    val df2: DataFrame = spark.read.json("D:\\t2.json")
    val map = new mutable.HashMap[String,String]()
    df1.foreach(line =>{
      val userId: String = line.getAs[String]("useid")
      val logId: String = line.getAs[String]("logid")
      map += userId -> logId
    })

    val broad: Broadcast[mutable.HashMap[String, String]] = spark.sparkContext.broadcast(map)

    import spark.implicits._
    val df3: DataFrame = df2.mapPartitions(partition => {
      val value: mutable.HashMap[String, String] = broad.value
      partition.map(line => {
        val logId: String = line.getAs[String]("logid")
        val likeId: String = line.getAs[String]("likeid")
        val userId: String = value.getOrElse(logId,"")
        (userId, logId, likeId)
      })
    })toDF("USERID", "LOGID", "LIKEID")
    df3.show()


  }

}
