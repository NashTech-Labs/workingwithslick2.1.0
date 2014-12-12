package com.knol.db.core

import scala.slick.driver.PostgresDriver

import com.typesafe.config.ConfigFactory

trait PostgresDBComponent extends DBComponent {

  val driver = PostgresDriver

  import driver.simple._

  private val config = ConfigFactory.load()
  private val dbDriver = config.getString("db.driver")
  private val url = config.getString("db.url")
  private val host = config.getString("db.host")
  private val port = config.getInt("db.port")
  private val database = config.getString("db.database")
  private val userName = config.getString("db.username")
  private val password = config.getString("db.password")
  private val combindURL = url + host + ":" + port + "/" + database
  
  def dbObject(): Database = Database.forURL(combindURL, userName, password, null, dbDriver)

}