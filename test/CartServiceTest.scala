import java.util.UUID

import actors.CartActor
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.pattern.ask
import akka.testkit.TestKit
import akka.util.Timeout
import entites.{Cart, CartEntry, CartMessage}
import net.liftweb.json.{NoTypeHints, Serialization}
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import services.CartService

import scala.concurrent.Await
import scala.concurrent.duration._


object MySpec {

  class EchoActor extends Actor {
    def receive = {
      case x ⇒ {
        println(x)
        sender ! "Hello"
      }
    }
  }

}

class CartServiceTest(_system: ActorSystem) extends TestKit(_system)
  with WordSpecLike with MustMatchers with BeforeAndAfterAll {
  implicit var formats = Serialization.formats(NoTypeHints)
  implicit val timeout = Timeout(50 seconds)

  def this() = this(ActorSystem("MySpec"))

  private def createSharding(system: ActorSystem) = {
    ClusterSharding(system).start(
      typeName = CartActor.shardName,
      entityProps = CartActor.props(),
      settings = ClusterShardingSettings(system),
      extractEntityId = CartActor.idExtractor,
      extractShardId = CartActor.shardResolver)

  }

  override def afterAll {
    system.terminate()
  }

  val globalCartId = "8540101d-f114-494d-a825-df63af523815";

  "Cart Service (Basic Test)" must {

    // Join cluster
    val cluster = Cluster(system)
    cluster.join(cluster.selfAddress)
    createSharding(system)

    val cartService: ActorRef = system.actorOf(CartService.props, "controller")


    "Return Cart Object Back" in {
      val cartMessage = new CartMessage("ADDSKUTOCART", UUID.fromString(globalCartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        "Bolt", 1, 10.10f, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null)
    }

    "have correct cart id" in {
      val cartId = UUID.randomUUID().toString;
      val cartMessage = new CartMessage("ADDSKUTOCART", UUID.fromString(cartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        "Bolt", 1, 10.10f, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null)
      cartObj.cart_id must not be (null)
      cartObj.cart_id.toString() must be(cartId)
    }

    "have correct number cart entry" in {
      val cartId = UUID.randomUUID().toString;
      val cartMessage = new CartMessage("ADDSKUTOCART", UUID.fromString(cartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        "Bolt", 1, 10.10f, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null)
      cartObj.cart_entries must not be (null)
      cartObj.cart_entries must have size (1)
    }
  }

  "Cart Service (Some Logic Level Test)" must {

    // Join cluster
    val cluster = Cluster(system)
    cluster.join(cluster.selfAddress)
    createSharding(system)

    val cartId = UUID.randomUUID().toString;
    val SKUId: String = "Belt";
    val quantity: Int = 5;
    val price: Double = 234;
    val grandTotal = (quantity * price)

    val SKUId2: String = "Air Filter";
    val quantity2: Int = 3;
    val price2: Double = 738;
    val grandTotal2 = ((quantity2 * price2) + grandTotal)


    val cartService: ActorRef = system.actorOf(CartService.props, cartId)


    "have correct number of items and totals for single item" in {
      // ************************ For Single Entry ********************************
      val cartMessage = new CartMessage("ADDSKUTOCART", UUID.fromString(cartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        SKUId, quantity, price, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null) // check if got cart object

      // test if result have cart entry list
      cartObj.cart_entries must not be (null)
      withClue("Cart should have single entry But :  ") {
        cartObj.cart_entries must have size (1)
      }

      // test if cart entry have correct quantity
      var cartEntries: Map[String, CartEntry] = cartObj.cart_entries
      var ce = cartEntries.getOrElse(SKUId, null)
      withClue("Quanity for item (1st) " + SKUId + " should be " + quantity + " But : ") {
        ce.quantity must be(quantity)
      }

      // if grand total is correct
      withClue("Grand total for single item should be " + grandTotal + " : But ") {
        cartObj.cart_total must be(grandTotal)
      }
    }

    // ******************* after adding one more new SKU and then check if cart returning correct *************************
    "have correct number of items and totals for 2 or more items" in {
      val cartMessage2 = new CartMessage("ADDSKUTOCART", UUID.fromString(cartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        SKUId2, quantity2, price2, "Car Air Filter", "test");

      val cart2 = cartService ? cartMessage2
      val result2 = Await.result(cart2, 50 seconds)
      println("Got Response (TestCases) :" + result2.asInstanceOf[Cart].toString)
      val cartObj2 = result2.asInstanceOf[Cart];
      cartObj2 must not be (null) // check if got cart object

      // test if result have cart entry list
      cartObj2.cart_entries must not be (null)
      withClue("Cart should have 2 products entry But :  ") {
        cartObj2.cart_entries must have size (2)
      }

      // test if cart entry have correct quantity
      var cartEntries: Map[String, CartEntry] = cartObj2.cart_entries
      var ce = cartEntries.getOrElse(SKUId, null)
      withClue("Quanity for item (1st) " + SKUId + " should be " + quantity + " But : ") {
        ce.quantity must be(quantity)
      }

      // test if cart entry have correct quantity for item 1
      var cartEntries2: Map[String, CartEntry] = cartObj2.cart_entries
      var ce2 = cartEntries2.getOrElse(SKUId2, null)
      withClue("Quanity for item (2nd) " + SKUId2 + " should be " + quantity2 + " But : ") {
        ce2.quantity must be(quantity2)
      }

      // if grand total is correct
      withClue("Grand total for both items should be " + grandTotal2 + " : But ") {
        cartObj2.cart_total must be(grandTotal2)
      }
    }

    /*
    ********************************** Chnage SKU quantity Test ********************************
     * will increase quantity by 5 for 1st and decrease by 2 for 2nd item
     */

    val quantityIncreased = (quantity + 5)
    val quantityDecreased = (quantity2 - 6)
    val grandTotalAfterIncrease = (quantityIncreased * price) + (quantity2 * price2)
    val grandTotalAfterDecrease = ((quantityDecreased * price2) + (quantityIncreased * price))

    "have correct quantity after update quantity (increase 1st SKU) " in {
      // ************************ For Single Entry ********************************
      val cartMessage = new CartMessage("CHANGESKUQUANTITY", UUID.fromString(cartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        SKUId, quantityIncreased, price, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null) // check if got cart object

      // test if result have cart entry list
      cartObj.cart_entries must not be (null)
      withClue("Cart should have single entry But :  ") {
        cartObj.cart_entries must have size (2)
      }

      // test if cart entry have correct quantity
      var cartEntries: Map[String, CartEntry] = cartObj.cart_entries
      var ce = cartEntries.getOrElse(SKUId, null)
      withClue("Quanity for item (1st) " + SKUId + " should be " + quantityIncreased + " But : ") {
        ce.quantity must be(quantityIncreased)
      }

      // if grand total is correct
      withClue("Grand total for single item should be " + grandTotalAfterIncrease + " : But ") {
        cartObj.cart_total must be(grandTotalAfterIncrease)
      }
    }

    "have correct quantity after update quantity (decrease 2nd SKU) " in {
      // ************************ For Single Entry ********************************
      val cartMessage = new CartMessage("CHANGESKUQUANTITY", UUID.fromString(cartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        SKUId2, quantityDecreased, price2, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null) // check if got cart object

      // test if result have cart entry list
      cartObj.cart_entries must not be (null)
      withClue("Cart should have single entry But :  ") {
        cartObj.cart_entries must have size (2)
      }

      // test if cart entry have correct quantity
      var cartEntries: Map[String, CartEntry] = cartObj.cart_entries
      var ce = cartEntries.getOrElse(SKUId, null)
      withClue("Quanity for item (1st) " + SKUId + " should be " + quantityIncreased + " But : ") {
        ce.quantity must be(quantityIncreased)
      }

      // test if cart entry have correct quantity
      var ce2 = cartEntries.getOrElse(SKUId2, null)
      withClue("Quanity for item (2st) " + SKUId2 + " should be " + quantityDecreased + " But : ") {
        ce2.quantity must be(quantityDecreased)
      }

      // if grand total is correct
      withClue("Grand total for both  items after quantity change should be " + grandTotalAfterDecrease + " : But ") {
        cartObj.cart_total must be(grandTotalAfterDecrease)
      }
    }

    /*
    ******************* Test result after removing item from cart *********************************
    *  going to delete 2nd card then verify cart Items and Grand Totals
     */

    val grandTotalAfterDeleting = (quantityIncreased * price)
    "have correct QKU entry after deleting 2nd from list" in {
      // ************************ For Single Entry ********************************
      val cartMessage = new CartMessage("REMOVESKUFROMCART", UUID.fromString(cartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        SKUId2, quantityIncreased, price, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null) // check if got cart object

      // test if result have cart entry list
      cartObj.cart_entries must not be (null)
      withClue("Cart should have single entry But :  ") {
        cartObj.cart_entries must have size (1)
      }

      // if grand total is correct
      withClue("Grand total for single item should be " + grandTotalAfterDeleting + " : But ") {
        cartObj.cart_total must be(grandTotalAfterDeleting)
      }
    }

    /*
   ******************* Test To Get An Existing Card From DataBase or Actor *********************************
   *  Will Pass Cart Id And Expect Cart Data In Response
    */

    "return cart data" in {
      val cartMessage = new CartMessage("GETCART", UUID.fromString(globalCartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        SKUId2, quantityIncreased, price, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null) // check if got cart object

      // test if result have cart entry list
      cartObj.cart_entries must not be (null)
      // test if return correct cart id
      cartObj.cart_id must not be (null)
      cartObj.cart_id.toString() must be(globalCartId)
    }
  }


  /*      Test Cases For Some Negative Cases
    Now We Will Pass Wrong Data To Service And Expect 400 (Bad Request) Or Unsuccessfull Result.
    If Got Success With Correct Data Then It Should Be Considered As Failior Of Case.
   */
  "Cart Service (Some Negative Logic Level Test)" must {

    // Join cluster
    val cluster = Cluster(system)
    cluster.join(cluster.selfAddress)
    createSharding(system)

    val cartId = UUID.randomUUID().toString()

    val cartService: ActorRef = system.actorOf(CartService.props, cartId)


    "grand total should be greater than or equal to 0" in {
      val quantity = -5
      val cartMessage = new CartMessage("ADDSKUTOCART", UUID.fromString(cartId), UUID.fromString("8540101d-f114-494d-a825-df63af523815"),
        "Bolt", quantity, 10.10f, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must not be (null)
      withClue("Grand total for single item should be greater or equals to 0 : But ") {
        cartObj.cart_total.toInt must be >= (0)
      }
    }

    "shoud always accept valid UUID" in {
      val quantity = -5
      val cartId = "8540101d-f114-494d-a825-df63af523815&";
      val cartMessage = new CartMessage("ADDSKUTOCART", UUID.fromString(cartId), UUID.fromString(cartId),
        "Bolt", quantity, 10.10f, "Car Bolt", "test");

      val cart = cartService ? cartMessage
      val result = Await.result(cart, 50 seconds)
      println("Got Response (TestCases) :" + result.asInstanceOf[Cart].toString)
      val cartObj = result.asInstanceOf[Cart];
      cartObj must be(null)
    }
  }

}

