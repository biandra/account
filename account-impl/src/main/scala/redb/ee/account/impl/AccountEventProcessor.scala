package redb.ee.account.impl

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import play.api.Logging
import redb.ee.account.domain.{AccountCreated, AccountEvent, AccountUpdated}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * @author biandra
  */
class AccountEventProcessor(
                             session: CassandraSession,
                             readSide: CassandraReadSide
                           )(implicit executionContext: ExecutionContext)
  extends ReadSideProcessor[AccountEvent] with Logging with CQLAccounts {

  private val insertAccountStatement = Promise[PreparedStatement]
  private val updateCustomerCreatedStatement = Promise[PreparedStatement]

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[AccountEvent] = readSide
    .builder[AccountEvent]("accountEventOffset")
    .setGlobalPrepare(() => createTable())
    .setPrepare(prepareStatements)
    .setEventHandler(handlerEventAccountCreated)
    .setEventHandler(handlerEventAccountUpdated)
    .build()

  override def aggregateTags: Set[AggregateEventTag[AccountEvent]] = AccountEvent.Tag.allTags


  private def createTable(): Future[Done] =
    session
      .executeCreateTable(createAccountTable)
      .map(_ => {
        logger.info(s"Created table: $createAccountTable")
        Done
      })

  private def prepareStatements(tag: AggregateEventTag[AccountEvent]): Future[Done] = {
    (for {
      ps1 <- createStatement(insertAccount, insertAccountStatement)
      ps2 <- createStatement(updateAccount, updateCustomerCreatedStatement)
    } yield ps2).map(_ => Done)
  }

  private def createStatement(cql: String, promisePrepareStatement :Promise[PreparedStatement]): Future[PreparedStatement] = {
    val preparedStatement = session.prepare(cql)
    promisePrepareStatement.completeWith(preparedStatement)
    preparedStatement.map(ps => {
      logger.info(s"Created statement from $cql")
      ps
    })
  }

  private def handlerEventAccountCreated(streamEvent: EventStreamElement[AccountCreated]): Future[immutable.Seq[BoundStatement]] = {
    insertAccountStatement.future.map(preparedStatement => {
      logger.info(s"Handling event: ${streamEvent.event}. Building the statements to insert a new account in unverified state")

      val statement = preparedStatement.bind()
      val account = streamEvent.event.account

      statement.setUUID("id", account.id)
      statement.setString("surname", account.surname)
      statement.setString("name", account.name)
      statement.setString("email", account.email)

      logger.info("Constructed the statements to insert a account in unverified state")
      val statements = List(statement)
      statements
    })
  }

  private def handlerEventAccountUpdated(streamEvent: EventStreamElement[AccountUpdated]): Future[immutable.Seq[BoundStatement]] = {
    updateCustomerCreatedStatement.future.map(preparedStatement => {
      logger.info(s"Handling event: ${streamEvent.event}. Building the statements to update a account in unverified state")

      val statement = preparedStatement.bind()
      val account = streamEvent.event.account

      statement.setString("surname", account.surname)
      statement.setString("name", account.name)
      statement.setString("email", account.email)
      statement.setUUID("id", account.id)

      logger.info("Constructed the statements to update a account in unverified state")
      val statements = List(statement)
      statements
    })
  }
}
