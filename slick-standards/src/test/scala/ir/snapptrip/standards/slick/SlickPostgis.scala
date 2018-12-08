package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import com.vividsolutions.jts.geom._
import org.scalatest.FunSuite

class SlickPostgis extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("simple usage of geom in postgis-feature with the help of pg-slick library") {
    autoRollback(
      for {
        _     <- CityTable.all.schema.create
        _     <- CityTable.all ++= Seq(
                   City("Tehran", gisCoordinate(51.3890, 35.6892)),
                   City("Rotterdam", gisCoordinate(4.4777, 51.9244))
                 )
        cities <- CityTable.all.result
      } yield {
        assert(cities == Seq(
          City("Tehran", gisCoordinate(51.3890, 35.6892)),
          City("Rotterdam", gisCoordinate(4.4777, 51.9244))
        ))
      }
    )
  }

  // MODELS ----------------------------------------------------------------------------------------------------------

  case class City(name: String, location: Point)

  // MODEL-TABLE MAPPING ---------------------------------------------------------------------------------------------

  class CityTable(tag: Tag) extends Table[City](tag, "CityTable") {
    val name = column[String]("name")
    val location = column[Point]("location")
    override def * = (name, location) <> (City.tupled, City.unapply)
  }

  object CityTable {
    val all = TableQuery[CityTable]
  }

}
