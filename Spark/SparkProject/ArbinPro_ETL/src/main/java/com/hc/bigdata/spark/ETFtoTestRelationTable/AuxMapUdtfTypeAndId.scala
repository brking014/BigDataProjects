package com.hc.bigdata.spark.ETFtoTestRelationTable

import org.apache.hadoop.hive.ql.exec.UDFArgumentException
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory
import org.apache.hadoop.hive.serde2.objectinspector.{ObjectInspector, ObjectInspectorFactory, StructObjectInspector}

import java.util

class AuxMapUdtfTypeAndId extends GenericUDTF {
  override def initialize(argOIs: Array[ObjectInspector]): StructObjectInspector = {
    //判断传入的参数是否只有一个
    if (argOIs.length != 1) {
      throw new UDFArgumentException("有且只能有一个参数")
    }
    //判断参数类型
    if (argOIs(0).getCategory != ObjectInspector.Category.PRIMITIVE) {
      throw new UDFArgumentException("参数类型不匹配")
    }
    val fieldNames = new util.ArrayList[String]()
    val fieldOIs = new util.ArrayList[ObjectInspector]()
    fieldNames.add("AuxCh_Type")
    fieldNames.add("AuxCh_ID")
    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector)
    fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector)
    ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs)
  }


  override def process(objects: Array[AnyRef]): Unit = {
    val strings = objects(0).toString.split(';')
    for (elem <- strings) {
      val tmp = new Array[String](2)
      val items = elem.split('^')
      for (j <- 0 until 2) {
        if (items.length > 1) {
          tmp(j) = items(j)
        }
      }

      forward(tmp)
    }
  }

  override def close(): Unit = {}
}
