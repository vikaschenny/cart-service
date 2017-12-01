package kafka.Cart

import java.util.UUID

import net.liftweb.json._

/**
  * Created by spineor on 22/12/16.
  */
class UUIDserializer extends Serializer[UUID] {
  private val UUIDClass = classOf[UUID]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UUID] = {
    case (TypeInfo(UUIDClass, _), json) => json match {
      case JString(s) => UUID.fromString(s)
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: UUID => JString(x.toString)
  }
}
