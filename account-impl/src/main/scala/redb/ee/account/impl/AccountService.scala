package redb.ee.account.impl

import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import redb.ee.account.api.Model.{AccountRequest, AccountResponse}
import redb.ee.account.converters.AccountConverter
import redb.ee.account.domain.Model.{Accepted, Confirmation}
import redb.ee.account.domain.{AccountCommand, CreateAccount, UpdateAccount}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author biandra
  */
class AccountService(
                      clusterSharding: ClusterSharding,
                      persistentEntityRegistry: PersistentEntityRegistry
                    )(implicit ec: ExecutionContext) extends AccountConverter{


  /**
    * Looks up the entity for the given ID.
    */
  private def entityRef(id: String): EntityRef[AccountCommand] =
    clusterSharding.entityRefFor(AccountState.typeKey, id)

  implicit val timeout = Timeout(5.seconds)


  def createAccount(accountRequest: AccountRequest): Future[Either[Throwable, AccountResponse]] ={
    val accountDetail = convert(accountRequest)
    entityRef(accountDetail.id.toString)
      .ask[Confirmation](
      replyTo => CreateAccount(accountDetail, replyTo)).map {
        case Accepted => Right(convert(accountDetail))
        case _        => Left(new RuntimeException())
      }
  }

  def updateAccount(accountId: String, accountRequest: AccountRequest): Future[Either[Throwable, AccountResponse]] ={
    val accountDetail = convert(accountId, accountRequest)
    entityRef(accountDetail.id.toString)
      .ask[Confirmation](
      replyTo => UpdateAccount(accountDetail, replyTo)).map {
        case Accepted => Right(convert(accountDetail))
        case _        => Left(new RuntimeException())
    }
  }
}
