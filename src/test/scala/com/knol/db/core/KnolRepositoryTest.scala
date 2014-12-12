package com.knol.db.core

import org.scalatest.FunSuite
import java.util.Date

class KnolRepositoryTest extends FunSuite with KnolRepositoryComponent with TestDBComponent {

  test("Get all knol info") {
    assert(getKnolList.length === 4)
  }

  test("Add new Knol info") {

    val knol = Knol("test", "test@knoldus.com", new Date("01/07/1986"))
    assert(insert(knol) === 5)
  }

  test("Update knol info") {
    val updatedKnol = Knol("Anand singh", "anand@knoldus.com", new Date("01/07/1986"), 1)
    assert(update(updatedKnol) === 1)
  }

  test("Delete Knol info") {
    assert(delete(1) === 1)
  }

}