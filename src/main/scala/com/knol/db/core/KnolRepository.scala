package com.knol.db.core

import com.knol.db.util.DateMapper

trait KnolRepositoryComponent extends DateMapper { this: DBComponent =>

  import driver.simple._
  import DateMapper._

  class KnolTable(tag: Tag) extends Table[Knol](tag, "knol") {

    def id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O.NotNull)
    def email: Column[String] = column[String]("email", O.NotNull)
    def dob: Column[java.util.Date] = column[java.util.Date]("dob", O.NotNull)
    def * = (name, email, dob, id) <> (Knol.tupled, Knol.unapply)
  }

  val knolTable = TableQuery[KnolTable]

  def getKnolList(): List[Knol] = {
    dbObject.withSession { implicit session: Session => knolTable.list }
  }
  
  def insert(knol: Knol): Int = {
    dbObject.withSession { implicit session: Session =>
      knolTable.returning(knolTable.map(_.id)).insert(knol)
    }
  }

  def update(knol: Knol): Int = {
    dbObject.withSession { implicit session: Session =>
      knolTable.filter { _.id === knol.id }.update(knol)
    }
  }

  def delete(id: Int): Int = {
    dbObject.withSession { implicit session: Session =>
      knolTable.filter { _.id === id }.delete
    }
  }

}

/**
 * for production usage
 *
 */
object KnolRepository extends KnolRepositoryComponent with PostgresDBComponent

case class Knol(name: String, email: String, dob: java.util.Date, id: Int = 0)