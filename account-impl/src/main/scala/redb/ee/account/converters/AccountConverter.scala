package redb.ee.account.converters

import java.util.UUID

import redb.ee.account.api.Model.{AccountRequest, AccountResponse}
import redb.ee.account.domain.Model.AccountDetail

/**
  * @author biandra
  */
trait AccountConverter {

  def convert(accountRequest: AccountRequest): AccountDetail =
    AccountDetail(id = UUID.randomUUID(),
      name = accountRequest.name,
      surname = accountRequest.surname,
      email = accountRequest.email)

  def convert(id: String, accountRequest: AccountRequest): AccountDetail =
    AccountDetail(id = UUID.fromString(id),
      name = accountRequest.name,
      surname = accountRequest.surname,
      email = accountRequest.email)


  def convert(accountDetail: AccountDetail): AccountResponse =
    AccountResponse(id = accountDetail.id,
      name = accountDetail.name,
      surname = accountDetail.surname,
      email = accountDetail.email)
}
