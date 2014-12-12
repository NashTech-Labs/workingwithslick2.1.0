package com.knol.db.util

import com.knol.db.core.DBComponent

trait DateMapper { this: DBComponent =>

  import driver.simple._
  
  object DateMapper {
    implicit val util2sqlDateMapper = MappedColumnType.base[java.util.Date, java.sql.Date](
      { utilDate => new java.sql.Date(utilDate.getTime()) },
      { sqlDate => new java.util.Date(sqlDate.getTime()) })
  }

}