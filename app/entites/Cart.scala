package entites

import java.util.UUID
//import scala.collection.mutable.Map


/**
  * Created by spineor on 12/12/16.
  */
case class Cart(var cart_id: UUID, var user_id: UUID, var cart_entries: Map[String, CartEntry],
                var total_numberOf_items: Integer, var cart_total: Double) {

}
