package com.knol.db.core

import scala.slick.driver.H2Driver
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

trait TestDBComponent extends DBComponent {

  val logger = LoggerFactory.getLogger(this.getClass())

  val driver = H2Driver

  import driver.simple._

  private val config = ConfigFactory.load()

  private val dbDriver = config.getString("db.driver")
  private val url = config.getString("db.url")

  def dbObject(): Database = {
    logger.debug(s"URL :[$url] driver: [$driver]")
    Database.forURL(url, driver = dbDriver)
  }
}