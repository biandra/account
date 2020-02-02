package redb.ee.account.api

import java.util.UUID

import play.api.libs.json.{Format, Json}

/**
  * @author biandra
  */
object Model {

  case class AccountRequest(
                            name: String,
                            surname: String,
                            email: String)

  object AccountRequest {
    implicit val format: Format[AccountRequest] = Json.format[AccountRequest]
  }

  case class AccountResponse(id: UUID,
                             name: String,
                             surname: String,
                             email: String)

  object AccountResponse {
    implicit val format: Format[AccountResponse] = Json.format[AccountResponse]
  }
}
