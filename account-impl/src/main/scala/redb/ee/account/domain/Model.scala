package redb.ee.account.domain

import java.util.UUID

import play.api.libs.json._

/**
  * @author biandra
  */
object Model {

  case class AccountDetail(
                          id: UUID,
                            name: String,
                            surname: String,
                            email: String
                          )

  object AccountDetail {
    implicit val format: Format[AccountDetail] = Json.format
  }


  sealed trait Confirmation

  case object Confirmation {
    implicit val format: Format[Confirmation] = new Format[Confirmation] {
      override def reads(json: JsValue): JsResult[Confirmation] = {
        if ((json \ "reason").isDefined)
          Json.fromJson[Rejected](json)
        else
          Json.fromJson[Accepted](json)
      }

      override def writes(o: Confirmation): JsValue = {
        o match {
          case acc: Accepted => Json.toJson(acc)
          case rej: Rejected => Json.toJson(rej)
        }
      }
    }
  }

  sealed trait Accepted extends Confirmation

  case object Accepted extends Accepted {
    implicit val format: Format[Accepted] =
      Format(Reads(_ => JsSuccess(Accepted)), Writes(_ => Json.obj()))
  }

  case class Rejected(reason: String) extends Confirmation

  object Rejected {
    implicit val format: Format[Rejected] = Json.format
  }
}
