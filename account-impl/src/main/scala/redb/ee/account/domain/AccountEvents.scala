package redb.ee.account.domain

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import play.api.libs.json.{Format, Json}
import Model.AccountDetail

/**
  * @author biandra
  */
/**
  * This interface defines all the events that the AccountAggregate supports.
  */
sealed trait AccountEvent extends AggregateEvent[AccountEvent] {
  def aggregateTag: AggregateEventShards[AccountEvent] = AccountEvent.Tag
}

object AccountEvent {
  val Tag: AggregateEventShards[AccountEvent] = AggregateEventTag.sharded[AccountEvent](EVENT_SHARDS)
}

case class AccountCreated(account: AccountDetail) extends AccountEvent

object AccountCreated {
  implicit val format: Format[AccountCreated] = Json.format
}

case class AccountUpdated(account: AccountDetail) extends AccountEvent

object AccountUpdated {
  implicit val format: Format[AccountUpdated] = Json.format
}