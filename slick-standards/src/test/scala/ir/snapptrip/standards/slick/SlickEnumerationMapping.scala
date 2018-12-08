package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite

class SlickEnumerationMapping extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("simple enumeration-to-string mapping") {
    autoRollback(
      for {
        _      <- ShirtTable.all.schema.create
        _      <- ShirtTable.all ++= Seq(Shirt(Color.Red), Shirt(Color.Blue), Shirt(Color.Red))
        shirts <- ShirtTable.all.result
      } yield {
        assert(shirts == Seq(Shirt(Color.Red), Shirt(Color.Blue), Shirt(Color.Red)))
      }
    )
  }

  // MODELS ----------------------------------------------------------------------------------------------------------

  case class Shirt(color: Color.Type)

  object Color extends Enumeration {
    type Type = Value
    val Blue  = Value("Blue")
    val Red   = Value("Red")
    val Green = Value("Green")
  }

  // MODEL-TABLE MAPPING + ENUMERATION FORMATTING --------------------------------------------------------------------

  /** we chose to store this enum in the text form, integral form is also possible:
     `MappedColumnType.base[Color.Type, Int](cl => cl.id, id => Color.apply(id))`
   **/
  implicit val colorMapper = MappedColumnType.base[Color.Type, String](
    e => e.toString,
    s => Color.withName(s)
  )

  class ShirtTable(tag: Tag) extends Table[Shirt](tag, "ShirtTable") {
    val color = column[Color.Type]("color")
    override def * = color <> (Shirt.apply, Shirt.unapply)
  }

  object ShirtTable {
    val all = TableQuery[ShirtTable]
  }

}
