package com.exercise.spark.practice

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

//UDAF定义
//输入INTIME 输出结果
class Interval extends UserDefinedAggregateFunction{
  //输入数据数据类型
  override def inputSchema: StructType = {
    StructType(StructField("input",StringType)::Nil)
  }

  //缓冲区聚合时 处理的数据类型
  override def bufferSchema: StructType = {
    StructType(StructField("interval",StringType)::StructField("count",LongType)::Nil)
  }

  //返回值的数据类型，只能返回一列
//    override def dataType: DataType = ???
  def dataType:StructType = {
    StructType(StructField("interval", StringType) :: StructField("count", LongType) :: Nil)
  }

  // 此函数是否始终在相同输入上返回相同的输出
  override def deterministic: Boolean = true

  // 初始化给定的聚合缓冲区，即聚合缓冲区的零值。
  // 请注意，缓冲区内的数组和映射仍然是不可变的。
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = ""//用来存储所有的日期毫秒值
    buffer(1) = 0L//用来存储住店的次数
  }

  //使用来自`input`的新输入数据更新给定的聚合缓冲区`buffer`。每个输入行调用一次。
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (!input.isNullAt(0)){
      val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val date: Date = format.parse(input.getString(0))
      val time: Long = date.getTime//毫秒数
      buffer(0) = buffer.getString(0) + time.toString + " "
      buffer(1) = buffer.getLong(1) + 1
    }
  }

  // Spark是分布式的，所以不同的区需要进行合并。
  // 合并两个聚合缓冲区并将更新的缓冲区值存储回“buffer1”。当我们将两个部分聚合的数据合并在一起时调用此方法。
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getString(0) + buffer2.getString(0).toString + " "
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }

  //计算最终的结果
  override def evaluate(buffer: Row): Any = {
    val arr: Array[Double] = buffer.getString(0).split("\\s+").map(_.toDouble).sorted
    val length: Int = arr.length
    var res = "0 "
    for (i<- 0 until length-1){
      val j: Int = i + 1
      val r: Long = ((arr(j) - arr(i))/86400000).round
      res = res + r.toString+" "
    }
    (res,buffer.getLong(1))
  }
}
