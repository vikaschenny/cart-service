package global

import actors.CartActor
import akka.actor.ActorSystem
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.google.inject.{Inject, Singleton}


@Singleton
class AppGlobal  @Inject()(actorSystem: ActorSystem)()  {

  var  system: ActorSystem = actorSystem
  createSharding(system)

  private def createSharding(system: ActorSystem) = {
    ClusterSharding(system).start(
      typeName = CartActor.shardName,
      entityProps = CartActor.props(),
      settings = ClusterShardingSettings(system),
      extractEntityId = CartActor.idExtractor,
      extractShardId = CartActor.shardResolver)

  }

}




