package redb.ee.account.impl

import scala.io.Source

/**
  * @author biandra
  */
trait CQLAccounts {

  lazy val createAccountTable: String = Source.fromInputStream(getClass.getResourceAsStream("/cqls/account_create.cql")).mkString

  lazy val insertAccount: String = Source.fromInputStream(getClass.getResourceAsStream("/cqls/account_insert.cql")).mkString

  lazy val updateAccount: String = Source.fromInputStream(getClass.getResourceAsStream("/cqls/account_update.cql")).mkString

}
