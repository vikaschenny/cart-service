package kafka.Cart

import java.util


import entites.Cart
import net.liftweb.json.{JsonParser, NoTypeHints, Serialization}
import org.apache.kafka.common.serialization.Deserializer

/**
  * Created by spineor on 20/12/16.
  */
class CartDeserializer extends Deserializer[Cart]{
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }

  override def close(): Unit = {

  }

  override def    deserialize(topic: String, data: Array[Byte]): Cart = {

    implicit var formats = Serialization.formats(NoTypeHints) +  new UUIDserializer
    val json = new String(data)
    val jsonOb = JsonParser.parse(json)
    val cart = jsonOb.extract[Cart]
    return cart


  }
}
