package redb.ee.account.impl

import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, ResponseHeader}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.softwaremill.macwire._
import javax.inject.Inject
import play.api.Logging
import redb.ee.account.api.Model.{AccountRequest, AccountResponse}
import redb.ee.account.api.OnboardingService

import scala.concurrent.ExecutionContext

/**
  * Implementation of the OnboardingService.
  */
class OnboardingServiceImpl @Inject()(
  clusterSharding: ClusterSharding,
  persistentEntityRegistry: PersistentEntityRegistry,
)(implicit ec: ExecutionContext)
  extends OnboardingService with Logging{

  val accountService: AccountService = wire[AccountService]


  override def createAccount(): ServiceCall[AccountRequest, AccountResponse] = ServerServiceCall { (requestHeader, accountRequest) =>

    accountService.createAccount(accountRequest) map {
      case Right(account) => (ResponseHeader.Ok.withStatus(201), account)
      case Left(error) =>
        logger.error("can't create account", error)
        throw BadRequest("can't create account")
    }
  }

  override def updateAccount(accountId: String): ServiceCall[AccountRequest, AccountResponse] = ServerServiceCall { (requestHeader, accountRequest) =>

    accountService.updateAccount(accountId, accountRequest)  map {
      case Right(account) => (ResponseHeader.Ok, account)
      case Left(error) =>
        logger.error("can't update account", error)
        throw BadRequest("can't create account")
    }
  }
}
