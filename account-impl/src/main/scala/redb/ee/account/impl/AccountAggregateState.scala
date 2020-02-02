package redb.ee.account.impl

import java.time.LocalDateTime
import java.util.UUID

import akka.cluster.sharding.typed.scaladsl.EntityTypeKey
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, ReplyEffect}
import play.api.libs.json.{Format, Json}
import redb.ee.account.domain._
import redb.ee.account.domain.Model.{Accepted, AccountDetail}

/**
  * @author biandra
  */

/**
  * The current state of the Aggregate.
  */
case class AccountState(id: Option[UUID] = None,
                        name: Option[String],
                        surname: Option[String],
                        email: Option[String],
                        timestamp: String) {

  def applyCommand(cmd: AccountCommand): ReplyEffect[AccountEvent, AccountState] =
    cmd match {
      case createAccount: CreateAccount => Effect
        .persist(AccountCreated(createAccount.account))
        .thenReply(createAccount.replyTo) { _ =>
          Accepted
        }
      case updateAccount: UpdateAccount => Effect
        .persist(AccountUpdated(updateAccount.account))
        .thenReply(updateAccount.replyTo) { _ =>
          Accepted
        }
    }

  def applyEvent(evt: AccountEvent): AccountState =
    evt match {
      case AccountCreated(account) => accountCreated(account)
      case AccountUpdated(account) => accountUpdated(account)
    }


  private def accountCreated(account: AccountDetail): AccountState =
    copy(
      id = Some(account.id),
      name = Some(account.name),
      surname = Some(account.surname),
      email = Some(account.email),
      timestamp = LocalDateTime.now().toString)

  private def accountUpdated(account: AccountDetail): AccountState =
    copy(name = Some(account.name),
      surname = Some(account.surname),
      email = Some(account.email),
      timestamp = LocalDateTime.now().toString)
}

object AccountState {

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  def initial: AccountState = AccountState(
    None,
    None,
    None,
    None,
    LocalDateTime.now.toString)

  /**
    * The [[EventSourcedBehavior]] instances (aka Aggregates) run on sharded actors inside the Akka Cluster.
    * When sharding actors and distributing them across the cluster, each aggregate is
    * namespaced under a typekey that specifies a name and also the type of the commands
    * that sharded actor can receive.
    */
  val typeKey = EntityTypeKey[AccountCommand]("AccountAggregate")

  /**
    * Format for the AccountState state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the aggregate gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[AccountState] = Json.format
}