package kafka.cartMessage

import java.util.Properties

import com.typesafe.config.ConfigFactory
import entites.Cart
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Created by spineor on 2/1/17.
  */
class CartKafkaProducer {
  val config = ConfigFactory.load("cart-service.conf")
  def send(topic: String,cart :Cart) {
    val props = new Properties
    props.put("bootstrap.servers", config.getString("kafka.bootstrap.servers"))
    props.put("acks", "all")
    props.put("retries", new Integer(0))
    props.put("batch.size", new Integer(16384))
    props.put("linger.ms", new Integer(1))
    props.put("buffer.memory", new Integer(33554432))
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "kafka.Cart.CartSerializer")


    val producer = new KafkaProducer[String, Cart](props)



    producer.send(new ProducerRecord[String, Cart](topic, cart))
    println(topic)
    System.out.println("Cart Service Produced Cart Message")
    producer.close();
  }
}
/*object ProducerTest extends App{
  val pro = new CartKafkaProducer
  val map: Map[String, CartEntry] = Map()

  val cart = new Cart(UUID.randomUUID(),UUID.randomUUID(),map)
  pro.send("cart_checkout", cart)
}*/
