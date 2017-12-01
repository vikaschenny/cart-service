package services

import java.util.UUID

import actors.CartActor._
import actors._
import akka.actor.{Actor, Props}
import akka.cluster.sharding.ClusterSharding
import akka.pattern.ask
import akka.util.Timeout
import entites.{Cart, CartMessage}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by spineor on 9/12/16.
  */
class CartService extends Actor {


  implicit val timeout = Timeout(50 seconds)
  override def receive: Receive = {

    case CartMessage(operation: String, cart_id: UUID, user_id:UUID, sku_id: String, quantity: Int, price: Float, title: String, imageUrl:String, core_deposit : Double, product_url : String, fitmentuid : String) =>
      if(operation == "ADDSKUTOCART"){
        println("Step 2: Adding to Cart in Cart Service")
        val cart = cartRegion ?  AddSkuToCartCmd(cart_id, user_id, sku_id, quantity, price.toDouble, title, imageUrl, core_deposit, product_url, fitmentuid)
        val result = Await.result(cart,50 seconds)
        println("Step 4 Got Cart from  Cart actor in service now returning" + result.asInstanceOf[Cart].toString)
        sender ! result.asInstanceOf[Cart]
      }
      if(operation == "REMOVESKUFROMCART"){
        val cart = cartRegion ? RemoveSkufromCmd(cart_id, user_id, sku_id)
        val result = Await.result(cart,50 seconds)
        sender ! result.asInstanceOf[Cart]
      }
      if(operation == "CHANGESKUQUANTITY"){
        val cart = cartRegion ? ChangeSkuQuantityCmd(cart_id, user_id, sku_id, quantity, price.toDouble, title, imageUrl, core_deposit, product_url, fitmentuid)
        val result = Await.result(cart,50 seconds)
        sender ! result.asInstanceOf[Cart]
      }
      if(operation == "GETCART"){
        val cart = cartRegion ? GetCart(cart_id)
        val result = Await.result(cart,50 seconds)
        sender ! result.asInstanceOf[Cart]
      }

  }


  val cartRegion = ClusterSharding(context.system).shardRegion(CartActor.shardName)
  
  var activeUserCarts: Map[UUID, UUID] = Map()


 }

object CartService{
  def props = Props[CartService]

}