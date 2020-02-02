package redb.ee.account.impl

import akka.actor.typed.Behavior
import akka.cluster.sharding.typed.scaladsl.EntityContext
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import com.lightbend.lagom.scaladsl.persistence.AkkaTaggerAdapter
import redb.ee.account.domain.{AccountCommand, AccountEvent}

/**
  * @author biandra
  */

/**
  * This provides an event sourced behavior. It has a state, [[AccountState]]
  * Commands get translated to events, and it's the events that get persisted.
  */
object AccountAggregate {

  /**
    * Given a sharding [[EntityContext]] this function produces an Akka [[Behavior]] for the aggregate.
    */
  def create(entityContext: EntityContext[AccountCommand]): Behavior[AccountCommand] = {
    val persistenceId: PersistenceId = PersistenceId(entityContext.entityTypeKey.name, entityContext.entityId)

    create(persistenceId)
      .withTagger(
        // Using Akka Persistence Typed in Lagom requires tagging your events
        // in Lagom-compatible way so Lagom ReadSideProcessors and TopicProducers
        // can locate and follow the event streams.
        AkkaTaggerAdapter.fromLagom(entityContext, AccountEvent.Tag)
      )

  }

  /*
   * This method is extracted to write unit tests that are completely independendant to Akka Cluster.
   */
  private[impl] def create(persistenceId: PersistenceId):
      EventSourcedBehavior[AccountCommand, AccountEvent, AccountState] =
    EventSourcedBehavior.withEnforcedReplies[AccountCommand, AccountEvent, AccountState](
      persistenceId = persistenceId,
      emptyState = AccountState.initial,
      commandHandler = (cart, cmd) => cart.applyCommand(cmd),
      eventHandler = (cart, evt) => cart.applyEvent(evt)
  )
}