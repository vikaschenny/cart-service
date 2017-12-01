package kafka.cartMessage

import java.util

import entites.CartMessage
import kafka.Cart.UUIDserializer
import net.liftweb.json.Serialization._
import net.liftweb.json._
import org.apache.kafka.common.serialization.Serializer

/**
  * Created by spineor on 20/12/16.
  */
class CartMessageSerializer extends Serializer[CartMessage] {


  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }

  override def close(): Unit = {

  }

  override def serialize(topic: String, cartMessage: CartMessage): Array[Byte] = {
    var retVal: Array[Byte] = null

    implicit var formats = Serialization.formats(NoTypeHints) + new UUIDserializer
    val json = write(cartMessage)
    retVal = json.getBytes()
    retVal
  }
}
