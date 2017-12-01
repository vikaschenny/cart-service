package kafka.Cart

import java.util

import entites._

import net.liftweb.json.Serialization._
import net.liftweb.json._
import org.apache.kafka.common.serialization.Serializer

/**
  * Created by spineor on 20/12/16.
  */
class CartSerializer extends Serializer[Cart] {




  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }

  override def close(): Unit = {

  }

  override def serialize(topic: String, cart: Cart): Array[Byte] = {
    var retVal:Array[Byte]=null
    var entries = cart.cart_entries
    implicit var formats = Serialization.formats(NoTypeHints) +  new UUIDserializer
    val json = write(cart)
    println(json)
    retVal = json.getBytes()
    retVal
  }
}
