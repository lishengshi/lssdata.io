package com.exercise.spark.code


import java.sql.Date
import java.util.Properties

import com.exercise.util.BaseUtil._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object dataframe {

    case class Person(nam:String,age:Int)

    def main(args: Array[String]): Unit = {
      //1.6以后少用  都是用session取
      val sc = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("testnormal"))
      val spark: SparkSession = SparkSession.builder().enableHiveSupport().master("local[*]").appName("testnormal").getOrCreate()
      import org.apache.spark.sql.types._
      import spark.implicits._
      val seq = Seq(("aaa", 1, 2), ("bbb", 3, 4), ("bbb", 1, 5), ("bbb", 2, 1), ("ccc", 4, 5), ("bbb", 4, 6))
      val df: DataFrame = spark.createDataset(seq).toDF("key1","key2","key3")
      sc.textFile("E:\\idea_home\\IExercise\\src\\main\\scala\\com\\exercise\\spark\\data\\person.txt")
        .map(_.split(",")).map(p=>Person(p(0),p(1).trim().toInt)).toDF().show()

      if (0){
          df.show(30)
          df.show(30,truncate=20)//每个元素最大20个字符，其他折叠
          df.show(30,truncate=false)
          df.printSchema()
          df.explain()
          df.explain(true)
          val schema = StructType(List(
              StructField("Integer", IntegerType, nullable = false),
              StructField("String", StringType, nullable = true),
              StructField("date", DateType, nullable = false)
          ))
          val list: List[(Int, String, Date)] = List((1, "First Value", java.sql.Date.valueOf("2010-01-01"))
              , (2, "Second Value", java.sql.Date.valueOf("2010-02-01")))
          val rdd1: RDD[Row] = sc.parallelize(list.map(l=>Row(l._1,l._2,l._3)))
          val df1: DataFrame = spark.createDataFrame(rdd1,schema)
          df1.printSchema()

/*              spark.read.parquet("~/data/xx")
              spark.sql("select * from user.parquet")
              spark.read.json("xx")
              //通用加载保存功能
              spark.read.format("csv")//使用csv文件,spark2.0+之后,通用加载json, parquet, jdbc, orc, libsvm, csv, text
                .option("sep",",")//csv中的切割符，默认逗号
                .option("inferschema","true")//是否自动推断类型，没有的话全部是String
                .load("E:\\temp\\spark\\invest_people_all.csv")
                .show()*/

              //从Hive中读取
//          spark.read.table("student").show(20)

          val url: String = "jdbc:mysql://192.168.3.4:3306/data_v2"
          val properties = new Properties()
          properties.setProperty("user","test_jxdata")
          properties.setProperty("password","test_jxdata")
          val df2: DataFrame = spark.read.jdbc(url,"lib_company_pre",properties)
          df2.show(30)
      }

      //对column操作
      if (1){

      }
    }




}



