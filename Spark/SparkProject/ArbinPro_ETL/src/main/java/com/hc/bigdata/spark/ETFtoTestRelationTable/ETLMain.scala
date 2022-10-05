package com.hc.bigdata.spark.ETFtoTestRelationTable

import org.apache.spark.sql.SparkSession

object ETLMain {
  def main(args: Array[String]): Unit = {
    //设置Hadoop集群用户名,否则无法写入HDFS存入HIVE数据仓库中
    System.setProperty("HADOOP_USER_NAME", "atguigu")
    //集群启动HIVE  hive --service metastore
    val spark = SparkSession
      .builder()
      .enableHiveSupport()
      .master("local[*]")
      .appName("sql")
      .getOrCreate()
    //选择HIVE库
    spark.sql("use hc_nihe_test")
    //定义UDTF函数
    spark.sql("CREATE TEMPORARY FUNCTION udtf AS 'com.hc.bigdata.spark.ETFtoTestRelationTable.AuxMapUdtfTypeAndId'")
    //计算获取 test_name 实验数据关系表
    val udtfDf = spark.sql("select f.test_name,t.mindatetime,t.maxdatetime,udtf(s.aux_map),f.test_id from hc_nihe_ods_testlist_table f left join hc_nihe_ods_testivchlist_table s on f.test_id = s.test_id left join (select test_id,min(date_time) mindatetime,max(date_time) maxdatetime  from hc_nihe_ods_iv_basic_table group by test_id) t on f.test_id = t.test_id where f.test_name = '" + args(0) + "' limit 10")
    //计算获取 实验数据关系表
    udtfDf.write.mode("overwrite").saveAsTable("hc_nihe_dwd_test_relation")
    spark.stop()
  }
}
