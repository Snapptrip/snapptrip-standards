package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite
import ir.snapptrip.standards.ExtendedPostgresProfile
import shapeless._
import slickless._

class SlickBigTableShapelessMapping extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("A simple table creation-insertion-query") {
    val sampleObj = BigClass(
      None,
      "a",     "b",     "c",
      "d",     "e",     "f",
      "g",     "h",     "i",
      "j",     "k",     "l",
      "m",     "n",     "o",
      "p",     "q",     "r",
      "s",     "t",     "u",
      "v",     "w",     "x",
      "y",     "z"
    )
    autoRollback(
      for {
        _   <- BigClassTable.all.schema.create
        _   <- BigClassTable.all += sampleObj
        obj <- BigClassTable.all.result.head
      } yield {
        assert(obj.copy(id = None) == sampleObj)
      }
    )
  }

  // MODELS ----------------------------------------------------------------------------------------------------------

  /**
    * case classes with more than 22 fields are allowed
    * but the number of arguments/elements in Function/Tuples are still limited to 22.
    *
    * This solution is integrated into slick already, wait for Slick-3.3 release:
    * https://github.com/slick/slick/pull/1889
    */
  case class BigClass(
    id: Option[Long] = None,
    a: String,     b: String,     c: String,
    d: String,     e: String,     f: String,
    g: String,     h: String,     i: String,
    j: String,     k: String,     l: String,
    m: String,     n: String,     o: String,
    p: String,     q: String,     r: String,
    s: String,     t: String,     u: String,
    v: String,     w: String,     x: String,
    y: String,     z: String
  )

  // MODEL-TABLE MAPPING ---------------------------------------------------------------------------------------------

  class BigClassTable(tag: Tag) extends Table[BigClass](tag, "BigClassTable") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc).?
    val a = column[String]("a");    val b = column[String]("b");    val c = column[String]("c")
    val d = column[String]("d");    val e = column[String]("e");    val f = column[String]("f")
    val g = column[String]("g");    val h = column[String]("h");    val i = column[String]("i")
    val j = column[String]("j");    val k = column[String]("k");    val l = column[String]("l")
    val m = column[String]("m");    val n = column[String]("n");    val o = column[String]("o")
    val p = column[String]("p");    val q = column[String]("q");    val r = column[String]("r")
    val s = column[String]("s");    val t = column[String]("t");    val u = column[String]("u")
    val v = column[String]("v");    val w = column[String]("w");    val x = column[String]("x")
    val y = column[String]("y");    val z = column[String]("z")
    override def * = (
      id ::
      a :: b :: c ::
      d :: e :: f ::
      g :: h :: i ::
      j :: k :: l ::
      m :: n :: o ::
      p :: q :: r ::
      s :: t :: u ::
      v :: w :: x ::
      y :: z :: HNil
    ).mappedWith(Generic[BigClass])
  }

  object BigClassTable {
    val all = TableQuery[BigClassTable]
  }

}