package entites
import java.util.UUID

case class CartMessage(operation: String, cart_id: UUID, user_id:UUID, sku_id: String, quantity: Int, price: Float,title: String, imageUrl:String, core_deposit: Double, product_url : String, fitmentuid : String) {


}