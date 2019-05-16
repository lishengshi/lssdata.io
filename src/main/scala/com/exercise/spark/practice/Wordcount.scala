package com.exercise.spark.practice

import java.util.UUID

import com.exercise.util.DataCreateUtil
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

object Wordcount {
  def main(args: Array[String]): Unit = {

    /*    * Description:
          * 给一篇英文文档，实现WordCount，最后的结果为DataFrame，Schema信息为PKID（String），WORD（String），FREQ（LONG）
        * 其中PK为主键使用UUID生成，WORD是单词，FREQ为词频，按照词频有大到小的顺序进行排序并输出所有结果。*/


    this.Wordcount2()
  }

  /** rdd to DF
    *  1.利用case class ，然后利用spark.implicits._ 调用toDF 构造 DataFrame
    *  2.利用createDataFrame 指定StructScheme
    *  条件：SparkSession.createDataFrame(rdd[Row],StructType)
    *  注意rdd里需要Row
    */
  def Wordcount1():Unit={
    val conf: SparkConf = new SparkConf().setAppName("lishengshi").setMaster("local[*]")
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    val rdd: RDD[(String, String, Int)] = spark.sparkContext.parallelize(Seq(DataCreateUtil.textCreate(400000, " ")))
      .flatMap(line => line.split("\\s+"))
      .map(x => (x, 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, false)
      .map(x => (UUID.randomUUID().toString, x._1, x._2))

    /**
      * 如果要使用datafram，需要创建sparksession
      * import  session变量名.implicits._
      * .toDF("","","")
      */
    import spark.implicits._
    rdd.toDF("PKID","WORD","FERQ").show()
  }


  def Wordcount2():Unit={
    val conf: SparkConf = new SparkConf().setAppName("lishengshi").setMaster("local[*]")
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    val rdd: RDD[Row] = spark.sparkContext.textFile("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\hello.txt")
      .flatMap(line => line.split("\\s+"))
      .map(x => (x, 1))
      .groupByKey
      .map(x => (x._1, x._2.sum))
      .sortBy(_._2, false)
      .map(x => Row(UUID.randomUUID().toString, x._1.toString, x._2.toLong))

    val struct = StructType(StructField("PKID", StringType, false) ::
      StructField("WORD", StringType, true) ::
      StructField("FREQ", LongType, true) :: Nil)

    spark.createDataFrame(rdd,struct).show()

  }

  /**
    * 这个是 问过的一个
    * 如果把flatMap 和 Map交换一个位置，结果怎么样？
    * 当时回答没影响，是有影响的
    */
  def demo1():Unit={
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo02")
    val sc = new SparkContext(conf)
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    val rdd1: RDD[Array[String]] = spark.sparkContext.textFile("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\userindo.txt")
      .map(line => line.split(","))
    val flat: RDD[(String, Int)] = rdd1.flatMap(f=>f.map((_,1)))
    val group: RDD[(String, Iterable[Int])] = flat.groupByKey
    group.map(f=>{f._1->f._2.sum}).foreach(println(_))
    sc.stop()
  }

}
