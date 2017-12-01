/*
package kafka.cartMessage

import java.util.{Arrays, Properties}

import com.typesafe.config.ConfigFactory
import entites.Cart
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}

class CartKafkaConsumer {
  val config = ConfigFactory.load("cart-service.conf")
  def read() {
    val configProperties = new Properties
    configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,config.getString("kafka.bootstrap.servers"))
    configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "kafka.Cart.CartDeserializer")
    configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "1234")
    configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple")
    //Figure out where to start processing messages from
    var kafkaConsumer = new KafkaConsumer[String, Cart](configProperties)
    kafkaConsumer.subscribe(Arrays.asList(config.getString("bootstrap.servers")))
    //Start processing messages
    try
        while (true) {
          val records = kafkaConsumer.poll(100)
          import scala.collection.JavaConversions._
          for (record <- records)
            System.out.println(record.value )
        }

    catch {
      case ex: Exception => {
        System.out.println("Exception caught " + ex.getMessage)
      }
    } finally {
      kafkaConsumer.close()

    }
  }


}
object TestConsumer extends App{
  val con = new CartKafkaConsumer
  con.read()
}*/
