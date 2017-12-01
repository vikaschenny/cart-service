package actors

import java.util.UUID

import akka.actor.Props
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import entites._
import kafka.Cart.UUIDserializer
import net.liftweb.json.{NoTypeHints, Serialization}
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by spineor on 12/12/16.
  */

//Command to be processed by the Actors


object CartActor{


  def props(): Props = Props(new CartActor)


  sealed trait Command {
    def cart_id: UUID
  }
  case class AddSkuToCartCmd(cart_id: UUID, user_id: UUID, sku_id: String, quantity: Int, price: Double, title: String, imageUrl: String, core_deposit : Double, product_url : String, fitmentuid : String) extends Command

  case class RemoveSkufromCmd(cart_id: UUID, user_id: UUID, sku_id: String) extends Command

  case class ChangeSkuQuantityCmd(cart_id: UUID, user_id: UUID, sku_id: String, new_quantity: Int, new_price: Double, title: String, imageUrl: String, core_deposit : Double, product_url : String, fitmentuid : String) extends Command

  case class GetCart(cart_id: UUID) extends Command


  //Events to be persisted in Journal
  case class AddedSkutoCartEvent(cart_id: UUID, user_id: UUID, sku_id: String, quantity: Int, price: Double, title: String, imageUrl: String, core_deposit : Double, product_url : String, fitmentuid : String)

  case class RemovedSkuFromCartEvent(cart_id: UUID, user_id: UUID, sku_id: String)

  case class ChangeSkuQuantityEvent(cart_id: UUID, user_id: UUID, sku_id: String, new_quantity: Int, new_price: Double, title: String, imageUrl: String, core_deposit : Double, product_url : String , fitmentuid : String)

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.cart_id.toString, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: Command => (math.abs(cmd.cart_id.hashCode) % 100).toString
  }

  val shardName: String = "Cart"

}

class CartActor extends PersistentActor {
  import CartActor._


  val config = ConfigFactory.load()
  implicit val timeout = Timeout(50 seconds)
  implicit var formats = Serialization.formats(NoTypeHints) + new UUIDserializer
  var cart: Cart = _

  override def preStart() {
    val actorName = self.path.name
    val cart_entries = Map[String, CartEntry]()
    val cart_new = new Cart(UUID.fromString(actorName), null, cart_entries, 0, 0)
    cart = cart_new

  }

  override def receiveRecover: Receive = {

    case AddedSkutoCartEvent(cart_id, user_id, sku_id, quantity, price, title, imageUrl, core_deposit, product_url, fitmentuid) =>
      addSkutoCart(cart_id, user_id, sku_id, quantity, price, title, imageUrl, core_deposit, product_url, fitmentuid)
      updateCartTotalandSize()

    case RemovedSkuFromCartEvent(cart_id, user_id, sku_id) =>
      removeSkufromCart(cart_id, user_id, sku_id)
      updateCartTotalandSize()

    case ChangeSkuQuantityEvent(cart_id, user_id, sku_id, new_quantity, new_price, title, imageUrl, core_deposit, product_url, fitmentuid) =>
      changeSkuQuantity(cart_id, user_id, sku_id, new_quantity, new_price, title, imageUrl, core_deposit, product_url, fitmentuid)
      updateCartTotalandSize()

    case GetCart(cart_id) =>

  }

  override def receiveCommand: Receive = {

    case AddSkuToCartCmd(cart_id, user_id, sku_id, quantity, price, title, imageUrl, core_deposit, product_url, fitmentuid) =>
      if(cart.cart_entries.contains(sku_id)){
        var new_quantity  = cart.cart_entries.get(sku_id).get.quantity + quantity
        self forward  ChangeSkuQuantityCmd(cart_id, user_id, sku_id, new_quantity, price, title, imageUrl, core_deposit, product_url, fitmentuid)
        println("Changed quantity of existing Sku ")
      }else{
        persist(AddedSkutoCartEvent(cart_id, user_id, sku_id,  quantity, price, title, imageUrl, core_deposit, product_url, fitmentuid))(evt => {
          addSkutoCart(cart_id, user_id, sku_id, quantity, price, title, imageUrl, core_deposit, product_url, fitmentuid)
          updateCartTotalandSize
          println("Step 3 Added to cart in Cart Actor ")
          sender ! cart
        })
      }


    case RemoveSkufromCmd(cart_id, user_id, sku_id) =>
      persist(RemovedSkuFromCartEvent(cart_id, user_id, sku_id))(evt => {
        removeSkufromCart(cart_id, user_id, sku_id)
        updateCartTotalandSize
        sender ! cart
      })


    case ChangeSkuQuantityCmd(cart_id, user_id, sku_id, new_quantity, new_price, title, imageUrl, core_deposit, product_url, fitmentuid) =>
      persist(ChangeSkuQuantityEvent(cart_id, user_id, sku_id, new_quantity, new_price, title, imageUrl, core_deposit, product_url, fitmentuid))(evt => {
        changeSkuQuantity(cart_id, user_id, sku_id, new_quantity, new_price, title, imageUrl, core_deposit, product_url, fitmentuid)
        updateCartTotalandSize
        sender ! cart
      })

    case GetCart(cart_id) =>
      sender ! cart
  }

  def addSkutoCart(cart_id: UUID, user_id: UUID, sku_id: String,  quantity: Int, price: Double, title: String, imageUrl: String, core_deposit : Double, product_url : String, fitmentuid : String) = {
    var entries = cart.cart_entries
    cart.user_id = user_id
    val fitment_group = sku_id.substring(0, 2)
    var fitment_id : Int = 0;
    if(fitment_group.equalsIgnoreCase("LQ"))
      fitment_id = 3;
    else if(fitment_group.equalsIgnoreCase("WP"))
      fitment_id = 2;
    else
      fitment_id = 1;
    val new_entry = CartEntry(sku_id, quantity , price, title, imageUrl, fitment_id, core_deposit, fitment_group, product_url, fitmentuid)
    val new_entries = entries + (sku_id -> new_entry)
    cart.cart_entries = new_entries
  }


  def changeSkuQuantity(cart_id: UUID, user_id: UUID, sku_id: String, new_quant: Int, new_price: Double, title: String, imageUrl: String, core_deposit : Double, product_url : String, fitmentuid : String) = {
    var entries = cart.cart_entries
    cart.user_id = user_id
    if (entries.contains(sku_id)) {
      //If sku is already present  increase quantity by 1 set price as latest received and update the entries map
      var cart_entry = entries.get(sku_id).get
      var new_quantity = new_quant
      cart.user_id = user_id
      val fitment_group = sku_id.substring(0, 2)
      var fitment_id : Int = 0;
      if(fitment_group.equalsIgnoreCase("LQ"))
        fitment_id = 3;
      else if(fitment_group.equalsIgnoreCase("WP"))
        fitment_id = 2;
      else
        fitment_id = 1;
      val new_entry = CartEntry(sku_id, new_quantity, new_price, title, imageUrl, fitment_id,  core_deposit, fitment_group, product_url, fitmentuid)
      val new_entries = entries + (sku_id -> new_entry)
      cart.cart_entries = new_entries

    }
    else {
      //TODO : Throw Exception , item to be updated is not present in cart
    }
    /*for ((k,v) <- entries) printf("key: %s, value: %s\n", k, v)*/
  }

  def removeSkufromCart(cart_id: UUID, user_id: UUID, sku_id: String) = {
    cart.user_id = user_id
    var entries = cart.cart_entries
    var new_entries = entries - sku_id
    cart.cart_entries = new_entries

  }

  def updateCartTotalandSize() = {
    var total_numberOf_items = 0
    var total: Double = 0.0
    var entries = cart.cart_entries.valuesIterator.toList.iterator
    for (entry <- entries) {
      total += entry.price * entry.quantity
      total_numberOf_items += entry.quantity
    }
    cart.cart_total = BigDecimal(total).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    cart.total_numberOf_items = total_numberOf_items
  }

  override def persistenceId: String = self.path.name

}



