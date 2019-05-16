package com.exercise.spark.practice


import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

//部分数据
//TableA
//0,郎爽
//1,刁悦梦
//2,戴崈
//3,解勒劼
//4,伏擎
//5,林芸
//TableB
//0,94
//0,97
//0,48
//1,45
//1,97
//1,96
//1,92
//1,34
//2,49
//2,85

//对于某学校的一次期末考试成绩，表A(ID long(主键), NAME String(姓名))为学生信息表，表B(ID long(外键), score int(成绩0-100))为学生成绩表，
//要求：
//1、计算每个学生的总成绩TOTALSCORE（所有成绩相加，比如学生小明的成绩为 70,65,75,80则总成绩为70+65+75+80=290）。
//2、计算每个学生的平均成绩AVGSCORE，结果保留2位小数（总成绩除以数据条数，比如小明的平均成绩为290/4=72.5）。
//3、根据平均成绩判断该学生是否及格PASS(AVGSCORE>=60)，如果及格标记为1，否则为0(小明成绩为72.5>60，所以小明及格)。
//3、计算结果格式为(ID long(主键), NAME String(姓名), TOTALSCORE long(总成绩), AVGSCOR DECIMAL(2,2)(Double也可)(平均成绩), PASS int(是否及格))。
//4、计算结果按照TOTALSCORE降序排序。
//5、计算原始数据从txt加载(字符串分隔符为",")，结果保存为parquet。
object Demo3 {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local").setAppName("demo")
    val sc: SparkContext = new SparkContext(sparkConf)
    val spark = SparkSession.builder().appName("Spark SQL basic example").config("spark.some.config.option", "some-value").getOrCreate()

    demo31(spark)



  }

  def demo32(spark:SparkSession):Unit={
    import spark.implicits._
    val nameRdd: DataFrame = spark.sparkContext.textFile("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\TableA.txt", 10)
      .map(line => line.split(",")).map(x => (x(0).toLong, x(1))).toDF("ID", "NAME")

    val scoreRdd:DataFrame= spark.sparkContext.textFile("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\TableB.txt", 10)
      .map(line => line.split(",")).map(x => (x(0).toLong, x(1).toLong)).toDF("ID","SCORE")


    //mapGroups 和 mapValues区别：
    scoreRdd.groupByKey(_.getLong(0)).mapGroups((id,groups)=>{

    })

  }

  def demo31(spark:SparkSession):Unit={
    val nameRdd: RDD[Row] = spark.sparkContext.textFile("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\TableA.txt", 10)
      .map(line => line.split(",")).map(x => Row(x(0), x(1)))

    val scoreRdd: RDD[Row] = spark.sparkContext.textFile("E:\\idea_home\\IExercise\\src\\main\\resources\\data.spark\\TableB.txt", 10)
      .map(line => line.split(",")).map(x => Row(x(0), x(1)))

    val nameType = StructType(StructField("id",StringType,false) :: StructField("name",StringType,false) :: Nil)
    val scoreType = StructType(StructField("id",StringType,false) :: StructField("score",StringType,false) :: Nil)

    spark.createDataFrame(nameRdd,nameType).createOrReplaceTempView("name")
    spark.createDataFrame(scoreRdd,scoreType).createOrReplaceTempView("score")

    //这个sql可以好好品味
/*    val sql =
      """select  a.id,sum(b.score) as TOTALSCORE,avg(b.socre) as AVGSCORE,
      |(case when avg(b.score)>=60  then 1 else 0 end) as PASS
      |from
      |name a,socre b where a.id = b.id
      |group by a.id  order by TOTALSCORE desc;"""*/

    val sql="select a.id,sum(b.score) as TOTALSCORE,avg(b.score) as AVGSCORE, (case when avg(b.score)>=60 then 1 else 0 end) as PASS  from name a,score b where a.id = b.id group by a.id order by TOTALSCORE desc"

    val res: DataFrame = spark.sql(sql)
    res.persist(StorageLevel.MEMORY_AND_DISK)
    res.write.parquet("E:\\temp\\spark\\demo3.parquet")
    spark.stop()
  }

}
