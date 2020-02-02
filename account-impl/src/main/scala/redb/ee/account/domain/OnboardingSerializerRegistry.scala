package redb.ee.account.domain

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import redb.ee.account.api.Model.{AccountRequest, AccountResponse}
import redb.ee.account.impl.AccountState
import redb.ee.account.domain.Model._

import scala.collection.immutable.Seq

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object OnboardingSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    // state and events can use play-json, but commands should use jackson because of ActorRef[T] (see application.conf)

    JsonSerializer[AccountCreated],
    JsonSerializer[AccountUpdated],
    JsonSerializer[AccountRequest],
    JsonSerializer[AccountResponse],
    JsonSerializer[AccountState],

    JsonSerializer[Confirmation],
    JsonSerializer[Accepted],
    JsonSerializer[Rejected]
  )
}
