/*
package kafka.cartMessage

import java.util.{Properties, UUID}

import entites.CartMessage
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Created by spineor on 2/1/17.
  */
class CartMessageProducer {

  def send() {
    val props = new Properties
    props.put("bootstrap.servers", "54.211.237.214:9092,54.211.236.81:9092")
    props.put("acks", "all")
    props.put("retries", new Integer(0))
    props.put("batch.size", new Integer(16384))
    props.put("linger.ms", new Integer(1))
    props.put("buffer.memory", new Integer(33554432))
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "kafka.CartMessage.CartMessageSerializer")

    val producer = new KafkaProducer[String, CartMessage](props)
    val cartMessage = new CartMessage("ADDSKUTOCART",UUID.randomUUID(),UUID.randomUUID(),"12QWE",0,34.90)


    producer.send(new ProducerRecord[String, CartMessage]("cart-message", "test", cartMessage))
    //System.out.println("Cart msg produced")
    producer.close();
  }
}
object SampleProducerForTesting{
  def main(args: Array[String]): Unit = {
    val cartMessageProducer = new CartMessageProducer
    cartMessageProducer.send()
  }
}
*/
