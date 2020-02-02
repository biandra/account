package redb.ee.account.api

import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import Model.{AccountRequest, AccountResponse}


/**
  * The onboarding service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the OnboardingService.
  */
trait OnboardingService extends Service {


  def createAccount():ServiceCall[AccountRequest, AccountResponse]

  def updateAccount(accountId: String):ServiceCall[AccountRequest, AccountResponse]

  override final def descriptor: Descriptor = {
    import Service._
    named("onboarding")
      .withCalls(
        restCall(Method.POST, "/api/accounts", createAccount _),
        restCall(Method.PUT, "/api/accounts/:accountId", updateAccount _),
      )
      .withAutoAcl(true)
  }
}
