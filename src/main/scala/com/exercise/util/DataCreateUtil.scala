package com.exercise.util

object DataCreateUtil {

  def getBaseData(name: String): Array[String] = {
    val arr = name match {
      case "水果" =>
        Array("苹果", "梨", "橘子", "芭蕉", "草莓", "龙眼", "香蕉", "榴莲", "荔枝", "橙子", "甘蔗",
          "枇杷", "葡萄", "芒果", "李子", "桃子", "提子", "哈密瓜", "香瓜", "木瓜", "火龙果", "猕猴桃", "雪梨",
          "西瓜", "石榴", "香梨", "山竹", "蟠桃", "贡梨", "鸭梨", "菠萝", "柚子", "樱桃", "椰子", "无花果",
          "山野葡萄", "桑葚", "人参果", "柿子", "杏子")
      case "四种水果" =>
        Array("苹果", "梨", "橘子", "芭蕉")
    }
    arr
  }

  def textCreate(totalTimes: Int, sep: String = ",", lineSplit: Boolean = false, perNum: Int = 10, kind: String = "水果"): String = {
    //用来造的基础数据，比如说水果，基本单词
    val baseDataArray = getBaseData(kind)

    val len = baseDataArray.length
    // 99万数据大约是1.7到2.2秒
    // "并不需要并行计算，99万也可以在几秒内运行完"
    //用来保存数据的列表
    var lst = List[String]()
    for (i <- 1 to totalTimes) {
      // 基于索引的快速访问需要使用数组
      val ran = scala.util.Random.nextInt(len)
      val str = if (!lineSplit) {
        //不换行
        sep + baseDataArray(ran)
      } else {
        //固定次数换行，也可以用随机换行的方法
        if (i % perNum == 0) {
          "\n" + baseDataArray(ran)
        } else {
          sep + baseDataArray(ran)
        }
      }
      // 快速添加使用列表，注意是往头部添加的。
      lst = str :: lst
    }
    lst.mkString("")
  }



}
