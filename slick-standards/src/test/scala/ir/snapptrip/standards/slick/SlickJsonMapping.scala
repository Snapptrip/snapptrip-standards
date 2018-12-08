package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import io.circe.Json.{fromDoubleOrNull, fromInt, fromJsonObject, fromString}
import io.circe.{Json, JsonObject}
import org.scalatest.FunSuite

class SlickJsonMapping  extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("simple usage of postgres' jsonb feature with the help of pg-slick library") {
    val sampleJson = fromJsonObject(
      JsonObject(
        "a" -> fromInt(1),
        "b" -> fromString("Nima"),
        "c" -> fromJsonObject(
          JsonObject(
            "d" -> fromDoubleOrNull(0.0),
            "e" -> fromDoubleOrNull(999.0)
          )
        )
      )
    )
    autoRollback(
      for {
        _       <- PersonTable.all.schema.create
        _       <- PersonTable.all ++= Seq(Person(sampleJson))
        persons <- PersonTable.all.result
      } yield {
        assert(persons.length == 1)
        val person_b = persons.head.data.\\("b").head.asString
        val person_c_e = persons.head.data.\\("c").head.\\("e").head.asNumber.map(_.toDouble)
        assert(person_b == Some("Nima"))
        assert(person_c_e == Some(999.0))
      }
    )
  }

  // MODELS ----------------------------------------------------------------------------------------------------------

  case class Person(data: Json)

  // MODEL-TABLE MAPPING ---------------------------------------------------------------------------------------------

  class PersonTable(tag: Tag) extends Table[Person](tag, "PersonTable") {
    val data = column[Json]("data")
    override def * = data <> (Person.apply, Person.unapply)
  }

  object PersonTable {
    val all = TableQuery[PersonTable]
  }

}
