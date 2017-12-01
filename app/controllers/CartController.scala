package controllers

import java.util.UUID
import javax.inject._

import akka.actor.{ActorRef, ActorSystem}
import entites.{Cart, CartMessage}
import global.AppGlobal
import kafka.Cart.UUIDserializer
import net.liftweb.json.Serialization._
import net.liftweb.json.{JsonParser, NoTypeHints, Serialization}
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.CartService
import akka.pattern.ask
import akka.util.Timeout
import play.api.GlobalSettings

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}


@Singleton
class CartController @Inject()(ws: WSClient)(actorSystem: ActorSystem)(global: AppGlobal)(implicit exec: ExecutionContext) extends Controller {

  val cartService: ActorRef = global.system.actorOf(CartService.props, "controller")
  implicit var formats = Serialization.formats(NoTypeHints) + new UUIDserializer
  implicit val timeout = Timeout(50 seconds)


  def replyToCartMessage() = Action {
    request =>
      val cartMessage = extractCartMessagefromRequest(request)
      val cart_future  = cartService ? cartMessage
      val result  = Await.result(cart_future,50 seconds)
      val cart = result.asInstanceOf[Cart]
      println("Step Final Returnig to Gapi")
      Ok(write(cart))
  }

  def extractCartMessagefromRequest(request: Request[AnyContent]): CartMessage = {
    println("Step 1 Recived the request " + request.body.asText)
    val json = request.body.asText.get
    val jsonOb = JsonParser.parse(json)
    val cartMessage = jsonOb.extract[CartMessage]
    cartMessage
  }




}
