package kafka.cartMessage

import java.util

import entites.CartMessage
import kafka.Cart.UUIDserializer
import net.liftweb.json.{JsonParser, NoTypeHints, Serialization}
import org.apache.kafka.common.serialization.Deserializer

/**
  * Created by spineor on 20/12/16.
  */
class CartMessageDeserializer extends Deserializer[CartMessage] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }

  override def close(): Unit = {

  }

  override def deserialize(topic: String, data: Array[Byte]): CartMessage = {

    implicit var formats = Serialization.formats(NoTypeHints) + new UUIDserializer
    val json = new String(data)
    val jsonOb = JsonParser.parse(json)
    val cartMessage = jsonOb.extract[CartMessage]
    return cartMessage


  }
}
