package com.knol.db.demo

import scala.slick.driver.H2Driver.simple._
import java.util.Date
import java.text.SimpleDateFormat
import scala.slick.lifted.ProvenShape

object Demo extends App {

  // "11/19/2014" => mm/dd/yyyy

  implicit val util2sqlDateMapper = MappedColumnType.base[java.util.Date, java.sql.Date](
    { utilDate => new java.sql.Date(utilDate.getTime()) },
    { sqlDate => new java.util.Date(sqlDate.getTime()) })

  class KnolTable(tag: Tag) extends Table[Knol](tag, "knol") {

    def id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O.NotNull)
    def email: Column[String] = column[String]("email", O.NotNull)
    def dob: Column[java.util.Date] = column[java.util.Date]("dob", O.NotNull)
    def * : ProvenShape[Knol] = (name, email, dob, id) <> (Knol.tupled, Knol.unapply)
  }

  class KnolXTable(tag: Tag) extends Table[KnolX](tag, "knolx") {

    def id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def topic: Column[String] = column[String]("topic", O.NotNull)
    def date: Column[Date] = column[Date]("date", O.NotNull)
    def knolId: Column[Int] = column[Int]("knol_id", O.NotNull)
    def * : ProvenShape[KnolX] = (topic, date, knolId, id) <> (KnolX.tupled, KnolX.unapply)
    def fKey = foreignKey("knol_session_fk", knolId, knolTable)(_.id)

    //  def <>[R:ClassTag , U](f: (U => R), g: (R => Option[U]))(implicit shape: Shape[_ <: FlatShapeLevel, T, U, _]) :MappedProjection[R, U]
  }
  val knolTable = TableQuery[KnolTable]
  val knolXTable = TableQuery[KnolXTable]
  //Database.forURL("jdbc:postgresql://localhost:5432/slickdemo", "postgres", "postgres",null,"org.postgresql.Driver")

  Database.forURL("jdbc:h2:mem:test", driver = "org.h2.Driver").withSession { implicit session =>

    // ddl
    (knolTable.ddl ++ knolXTable.ddl).create

    // insert a record
    knolTable.insert(Knol("saurabh", "saurabh@knoldus.com", new java.util.Date("01/07/1985")))

    /*                        
    knolTable +=Knol("saurabh", "saurabh@knoldus.com", new java.util.Date("01/07/1985"))
    
    // insert List of record
    val list=List(Knol("saurabh", "saurabh@knoldus.com", new java.util.Date("01/07/1985")))
    knolTable.insertAll(list:_*)
    
    knolTable ++=list
*/
    val query: Query[Column[String], String, Seq] = knolTable.map((knol: KnolTable) => knol.name: Column[String])

    val knolList: Seq[String] = query.run(session)

    // return auto generated primary key

    def knolAutoInc = knolTable.returning(knolTable.map(_.id))

    val knolId1 = knolAutoInc.insert(Knol("anand", "anand@knoldus.com", new java.util.Date("01/07/1986")))
    knolXTable.insert(KnolX("play framework", new java.util.Date("03/05/2014"), knolId1))
    knolXTable.insert(KnolX("Anorm", new java.util.Date("07/23/2014"), knolId1))
    knolAutoInc.insert(Knol("sky", "sky@knoldus.com", new java.util.Date("01/07/1989")))
    val knolId2 = knolAutoInc.insert(Knol("sky", "satendra@knoldus.com", new java.util.Date("01/07/1989")))
    knolXTable.insert(KnolX("slick 1.0.0", new java.util.Date("11/19/2013"), knolId2))
    knolXTable.insert(KnolX("Dependency Injection", new java.util.Date("06/23/2014"), knolId2))
    knolXTable.insert(KnolX("slick 2.1.0", new java.util.Date("11/19/2014"), knolId2))

    // show knolder list
    // println("Knol list::::::::"+knolTable.list)

    //implicit inner join
    val implicitInnerQuery: scala.slick.lifted.Query[(KnolTable, KnolXTable), (Knol, KnolX), Seq] = for {
      knol: KnolTable <- knolTable
      knolX: KnolXTable <- knolXTable if (knolX.knolId === knol.id)
    } yield (knol, knolX)
    //println("Implicit inner join ::::::::" + implicitInnerQuery.list)

    // explicit inner join 
    val innerQuery = for {
      (knol, knolX) <- knolTable innerJoin knolXTable on (_.id === _.knolId)
    } yield (knol, knolX)
    //  println("inner join ::::::::" + innerQuery.list)

    // left join 
    val leftJoin = for {
      (knol, knolX) <- knolTable leftJoin knolXTable on (_.id === _.knolId)
    } yield (knol, (knolX.topic.?, knolX.date.?, knolX.knolId.?, knolX.id.?))
    // println("left join:::::::::::" + leftJoin.list)

    //right join
    val rightJoin = for {
      (knol, knolX) <- knolTable rightJoin knolXTable on (_.id === _.knolId)
    } yield ((knol.name.?, knol.email.?, knol.dob.?, knol.id.?), knolX)
    // println("::::::::::::::::::::::::right join ::::::::::::::::::::::::::::::::::::")
    //rightJoin.list foreach println _

    // full outer join(H2 not support )
    val fullOuterJoin = for {
      (knol, knolX) <- knolTable outerJoin knolXTable on (_.id === _.knolId)
    } yield (knol, knolX)
    //println("::::::::::::::::::::::::full Outer Join  ::::::::::::::::::::::::::::::::::::")
    //fullOuterJoin.list foreach println _

    //update 
    val updateQuery = knolTable.filter { knol => knol.id === 2 }.map(_.name).update("Anandi")
    //println(knolTable.list)
    //filter
    val filteredQuery = knolTable.filter { knol => knol.dob < (new java.util.Date("01/07/1988")) }.map(_.name)
    //println(filteredQuery.list)

    //pagination
    // println(knolTable.drop(2).take(1).list)

    //sortby 
    val sortedQuery = knolTable.sortBy(knol => knol.name.desc)
    //println(sortedQuery.list)

    //compiled query
    def getKnolCompiledQuery = {
      Compiled((knolId: Column[Int]) => for { knol <- knolTable.filter(_.id === knolId) } yield (knol))
    }

    // println(getKnolCompiledQuery(2).list)

    //  query extension 
    implicit class QueryExtension1[T1, E1](val q: Query[T1, E1, Seq]) {
      def page(no: Int, pageSize: Int = 2): Query[T1, E1, Seq] = { q.drop((no - 1) * pageSize).take(pageSize) }
    }
    //  println(knolTable.page(2).list)

    ////  query extension by table
    implicit class QueryExtension2[T1, E1](val q: Query[KnolTable, Knol, Seq]) {
      def sortByName: Query[Column[String], String, Seq] =
        q.map(_.name).sortBy(_.asc)
    }

    // println(knolTable.sortByName.list)

    //  query extension for join

    implicit class QueryExtension3[T1, E1](val q: Query[T1, E1, Seq]) {
      def joinInner[T2, E2](s: Query[T2, E2, Seq])(condition: (T1, T2) => Column[Boolean]): Query[(T1, T2), (E1, E2), Seq] = {
        q.innerJoin(s).on(condition)
      }
    }

    val condition = (k: KnolTable, x: KnolXTable) => k.id === x.knolId

    val condition1: (KnolTable => Column[Boolean]) = k => k.id === 1
    println("condition :::::::::::::::"+knolTable.filter(condition1).list)

    //println(":::::::: query extension for join       :::::: \n"+knolTable.joinInner(knolXTable)(condition).list)

    // auto join

    implicit class QueryExtension4[T1, E1](val q: Query[T1, E1, Seq]) {
      def joinInner[T2, E2](s: Query[T2, E2, Seq])(condition: (T1, T2) => Column[Boolean]): Query[(T1, T2), (E1, E2), Seq] = {
        q.join(s).on(condition)
      }
    }
    def getKnolX(knolId: Column[Int]) = knolXTable.filter(_.id === knolId)

  }

}

case class KnolX(topic: String, date: Date, knolId: Int, id: Int = 0)
case class Knol(name: String, email: String, dob: Date, id: Int = 0)