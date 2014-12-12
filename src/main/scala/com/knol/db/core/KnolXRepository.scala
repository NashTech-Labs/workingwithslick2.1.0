package com.knol.db.core

import java.util.Date
import com.knol.db.util.DateMapper

trait KnolXRepositoryComponent extends KnolRepositoryComponent with DateMapper { this: DBComponent =>

  import driver.simple._
  import DateMapper._

  class KnolXTable(tag: Tag) extends Table[KnolX](tag, "knolx") {

    def id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def topic: Column[String] = column[String]("topic", O.NotNull)
    def date: Column[Date] = column[Date]("date", O.NotNull)
    def knolId: Column[Int] = column[Int]("knol_id", O.NotNull)
    def * = (topic, date, knolId, id) <> (KnolX.tupled, KnolX.unapply)
    def fKey = foreignKey("knol_x_fk", knolId, knolTable)(_.id)

  }

  val knolXTable = TableQuery[KnolXTable]

}

case class KnolX(topic: String, date: Date, knolId: Int, id: Int = 0)
