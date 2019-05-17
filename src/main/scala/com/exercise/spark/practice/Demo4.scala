package com.exercise.spark.practice


/*有sexMap存放性别编码及对应的文字内容，如下所示：
val sexMap:Map[String,String] = Map(("1"->"男"),("0"->"女"))
现有stuinfo.txt，内存有四位学生信息，每位学生有四个属性，分编为编号，名称，性别，地区，
 对应的Schema信息为code(String),name(Stgring),sex(String),area(String)，
 读取stuinfo.txt中学生信息，按照给定的Schema转换成DataFrame，
 然后使用UDF和广播变量相关知识在不改变Schema情况下将sex字段中的数字转换成sexMap中对应的文字。*/
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object Demo4 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local").setAppName("demo")
    val sc: SparkContext = new SparkContext(sparkConf)
    val spark = SparkSession.builder().appName("Spark SQL basic example").config(sparkConf).getOrCreate()
    val sexMap:Map[String,String] = Map(("1"->"男"),("0"->"女"))
    val broad: Broadcast[Map[String, String]] = sc.broadcast(sexMap)

    import spark.implicits._

    val textDF: DataFrame = sc.textFile("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\stuinfo.txt")
      .map(line => {
        val arr: Array[String] = line.split(",")
        (arr(0), arr(1), arr(2), arr(3))
      }).toDF("code", "name", "sex", "area")

    //注册一个udf函数的几种写法
    val fun2: (String => String) = (sex: String) => {
      val map: Map[String, String] = broad.value
      val gender: String = map.getOrElse(sex, null)
      gender
    }
    import   org.apache.spark.sql.functions._
    val sex2Map: UserDefinedFunction = udf(fun2)
    val sex2MapMore: UserDefinedFunction = spark.udf.register("jx",fun2)
    //withColumn  udf添加一列
    textDF.select("code","name","sex","area")
      .withColumn("gender",sex2Map(col("sex")))
      .select("code","name","sex","area","gender")
      .show()
    textDF.select("code","name","sex","area")
      .withColumn("gender",sex2MapMore(col("sex")))
      .drop("sex")
      .withColumnRenamed("gender","sex")
      .select("code","name","sex","area")
      .show()

    textDF.createOrReplaceTempView("student")
    spark.sql("select code,name,jx(sex),area from student").show()
    sc.stop()
    spark.stop()

  }

}
