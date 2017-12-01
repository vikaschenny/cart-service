/*
package kafka.cartMessage

import java.util.{Arrays, Properties}

import com.typesafe.config.ConfigFactory
import entites.CartMessage
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import services.CartService

import scala.collection.JavaConversions._


class CartMessageConsumer {
  val cartService = new CartService
  val config = ConfigFactory.load("cart-service.conf")
  def read() {
    val configProperties = new Properties
    configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.bootstrap.servers"))
    configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "kafka.cartMessage.CartMessageDeserializer")
    configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "123")
    configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple")
    //Figure out where to start processing messages from

    var kafkaConsumer = new KafkaConsumer[String, CartMessage](configProperties)
    kafkaConsumer.subscribe(Arrays.asList(config.getString("kafka.gapi_to_cart-topic")))
    //println("CartMessage consumer Started")
    //Start processing messages
    try
        while (true) {
          val records = kafkaConsumer.poll(100)
          for (record <- records) {
            println("Message Recived from cart: " + record.value.cart_id)
            forwardMessageToCartService(record.value)
          }

        }

    catch {
      case ex: Exception => {
         ex.printStackTrace()
      }
    } finally {
      kafkaConsumer.close()

    }
  }

  def forwardMessageToCartService(cartMsg: CartMessage) = {

    val operation = cartMsg.operation
    val cart_id = cartMsg.cart_id
    val user_id = cartMsg.user_id
    val sku_id = cartMsg.sku_id
    val quantity = cartMsg.quantity
    val price = cartMsg.price
   // val request_id = cartMsg.request_id

    if (operation == CartOperations.ADDSKUTOCART) {

      cartService.addSkutoCart(cart_id, user_id, sku_id, price, cartMsg.title , cartMsg.imageUrl)

    }

    else if (operation == CartOperations.REMOVESKUFROMCART) {
      cartService.removeSkuFromCart(cart_id, user_id, sku_id)
    }
    else if (operation == CartOperations.CHANGESKUQUANTITY) {
      cartService.changeSkuQuantity(cart_id, user_id, sku_id, quantity, price, cartMsg.title , cartMsg.imageUrl)
    }
    else if (operation == CartOperations.CHECKOUTCART) {
      cartService.checkoutCart(cart_id, user_id)
    }
    else if (operation == CartOperations.GETCART) {
      cartService.getCart(cart_id, user_id)
    }


  }


}

object MainConsumerCartService {
  def main(args: Array[String]): Unit = {
    val cartMessageConsumer = new CartMessageConsumer()
    cartMessageConsumer.read()
  }
}*/
