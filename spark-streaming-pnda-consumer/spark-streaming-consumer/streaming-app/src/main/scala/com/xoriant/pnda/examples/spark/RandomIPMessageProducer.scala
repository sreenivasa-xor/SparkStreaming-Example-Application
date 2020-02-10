package com.xoriant.pnda.examples.spark

import java.util.{Date, Properties}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

object RandomIPMessageProducer extends App {
  val events = 1000000.toInt
  val topic = "logstokafka"
  //val brokers = "10.42.0.80:9092"
  val brokers = "localhost:9092"
  val rnd = new Random()
  val props = new Properties()
  props.put("bootstrap.servers", brokers)
  props.put("client.id", "ScalaProducerExample")
  props.put("batch.size", "10000")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")


  val producer = new KafkaProducer[String, String](props)
  val t = System.currentTimeMillis()
  for (nEvents <- Range(0, events)) {
    val runtime = new Date().getTime()
    val ip = "192.168.2." + rnd.nextInt(255)
    val msg = runtime + "," + nEvents + ",www.example.com," + ip
    val data = new ProducerRecord[String, String](topic, ip, msg)

    producer.send(data)
  }

  System.out.println("sent per second: " + events * 1000 / (System.currentTimeMillis() - t))
  producer.close()
}