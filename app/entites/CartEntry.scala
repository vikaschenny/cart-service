package entites

/**
  * Created by spineor on 20/12/16.
  */
case class CartEntry(sku_id :String,
                     quantity :Int,
                     price :Double,
                     title: String,
                     imageUrl:String,
                     fitment_id : Int,
                     core_deposit: Double,
                     fitment_group : String,
                     product_url : String,
                     fitmentuid : String)
