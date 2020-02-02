package redb.ee.account.impl

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.softwaremill.macwire._
import redb.ee.account.api.OnboardingService
import redb.ee.account.domain.{AccountEvent, OnboardingSerializerRegistry}


class OnboardingLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new OnboardingApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new OnboardingApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[OnboardingService])
}

abstract class OnboardingApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides


  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = OnboardingSerializerRegistry

  val accountService = wire[AccountService]
  override lazy val lagomServer: LagomServer = serverFor[OnboardingService](wire[OnboardingServiceImpl])


  readSide.register[AccountEvent](wire[AccountEventProcessor])

  // Initialize the sharding of the Aggregate. The following starts the aggregate Behavior under
  // a given sharding entity typeKey.
  clusterSharding.init(
    Entity(AccountState.typeKey)(
      entityContext => AccountAggregate.create(entityContext)
    )
  )

}
