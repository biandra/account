package redb.ee.account.domain

import akka.actor.typed.ActorRef
import redb.ee.account.domain.Model.{AccountDetail, Confirmation}

/**
  * @author biandra
  */
/**
  * This is a marker trait for commands.
  * We will serialize them using Akka's Jackson support that is able to deal with the replyTo field.
  * (see application.conf)
  */
trait AccountCommandSerializable

/**
  * This interface defines all the commands that the AccountAggregate supports.
  */
sealed trait AccountCommand
  extends AccountCommandSerializable

/**
  * A command to switch the greeting message.
  *
  * It has a reply type of [[Confirmation]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case class CreateAccount(account: AccountDetail, replyTo: ActorRef[Confirmation])
  extends AccountCommand

case class UpdateAccount(account: AccountDetail, replyTo: ActorRef[Confirmation])
  extends AccountCommand
