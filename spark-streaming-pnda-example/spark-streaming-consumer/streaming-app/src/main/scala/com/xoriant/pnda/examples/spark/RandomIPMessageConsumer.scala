package com.xoriant.pnda.examples.spark;

import java.sql.Timestamp

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener

object RandomIPMessageConsumer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.
      builder().
      master("local").
      appName("Get Streaming Data").
      getOrCreate()

    import spark.implicits._
    spark.sparkContext.setLogLevel("ERROR")
    spark.conf.set("spark.sql.shuffle.partitions", "2")

    val data = spark.readStream.
      format("kafka").
      //option("kafka.bootstrap.servers", "10.42.0.80:9092").
      option("kafka.bootstrap.servers", "localhost:9092").
      option("subscribe", "logstokafka").
      option("enable.auto.commit", "true").
      option("auto.commit.interval.ms", "1000").
      option("maxOffsetsPerTrigger", "6000").
      option("startingOffsets", "earliest").
      option("includeTimestamp", true).
      load.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)", "CAST(offset AS STRING)")
      .as[(String, String, String)]

    val query = data.
      writeStream.
      outputMode("update").
      format("console").
      trigger(Trigger.ProcessingTime("20 seconds")).
      start()

    query.awaitTermination()
  }

}
