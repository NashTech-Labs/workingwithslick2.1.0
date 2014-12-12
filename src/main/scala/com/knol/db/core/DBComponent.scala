package com.knol.db.core

import scala.slick.driver.JdbcProfile

trait DBComponent {
  
  val driver: JdbcProfile
  
   import driver.simple._
   
    def dbObject(): Database

}