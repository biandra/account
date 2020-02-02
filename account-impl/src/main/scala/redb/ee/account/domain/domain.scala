package redb.ee.account

import com.typesafe.config.ConfigFactory

/**
  * @author biandra
  */
package object domain {

  private val configuration = ConfigFactory.load()

  /**
    * Gets the number of shards to use by the events.
    */
  val EVENT_SHARDS: Int = configuration.getInt("lagom.persistence.read-side.events.shards")
}
