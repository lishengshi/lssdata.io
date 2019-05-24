package com.exercise.util



import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession


object SparkSessionUtil {

  val appName = "lss"
  //设置日志级别
  Logger.getLogger("org.apache.spark").setLevel(Level.INFO)

  private lazy val builder: SparkSession.Builder = SparkSession.builder().config("spark.scheduler.mode", "FAIR")

  def getSparkSession: SparkSession = {
    //System.getProperty()  得到用户名称
    val name: String = scala.sys.props.get("user.name").head
    if (name.equals("jxkj")) {
      //本地模式
      builder.appName(appName).master("local[*]").getOrCreate()
    } else {
      //集群模式
      builder.appName(appName).getOrCreate()
    }
  }
  def getSc: SparkContext = {
    if (scala.sys.props.get("user.name").head.equals("hadoop")) {
      //旧的yarn-cluster
      new SparkContext(new SparkConf().setAppName(appName).setMaster("yarn-cluster"))
    } else {
      new SparkContext(new SparkConf().setAppName(appName).setMaster("local[*]"))
    }
  }

  private val 参数详解=0;
/*  设置自动广播：config("spark.sql.autoBroadcastJoinThreshold", "209715200")
  会自动广播小于10M的表，broadcast表的最大值10M（10485760），当为-1时，broadcasting不可用，内存允许的情况下加大这个值
  spark.sql.shuffle.partitions 当join或者聚合产生shuffle操作时， partitions的数量，
  这个值可以调大点， 我一般配置500， 切分更多的task， 有助于数据倾斜的减缓， 但是如果task越多， shuffle数据量也会增多*/


}
