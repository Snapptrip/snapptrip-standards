package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite
import shapeless._
import slickless._

class SlickBigTableSplitMapping extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("A simple table creation-insertion-query") {
    val sampleObj = BigClass(
      None,
      "a",     "b",     "c",
      "d",     "e",     "f",
      "g",     "h",
      BigClassPart1(
        "i",    "j",     "k",
        "l",    "m",     "n",
        "o",    "p",     "q",
      ),
      BigClassPart2(
        "r",    "s",    "t",
        "u",    "v",    "w",
        "x",    "y",    "z"
      )
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

  case class BigClass(
    id: Option[Long] = None,
    a: String,     b: String,     c: String,
    d: String,     e: String,     f: String,
    g: String,     h: String,
    bigClassPart1: BigClassPart1,
    bigClassPart2: BigClassPart2,
  )

  case class BigClassPart1(
    i: String,    j: String,     k: String,
    l: String,    m: String,     n: String,
    o: String,    p: String,     q: String,
  )

  case class BigClassPart2(
    r: String,    s: String,    t: String,
    u: String,    v: String,    w: String,
    x: String,    y: String,    z: String
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
      id,
      a, b, c,
      d, e, f,
      g, h,
      (
        i, j, k,
        l, m, n,
        o, p, q
      ),
      (
        r, s, t,
        u, v, w,
        x, y, z
      ),
    ).shaped <> (
      {
        case (id, a, b, c, d, e, f, g, h, bigClassPart1Tuple, bigClassPart2Tuple) =>
          BigClass(
            id,
            a, b, c, d, e, f, g, h,
            BigClassPart1.tupled(bigClassPart1Tuple),
            BigClassPart2.tupled(bigClassPart2Tuple),
          )
      },
      {
        bigClass: BigClass =>
          Some((
            bigClass.id,
            bigClass.a, bigClass.b, bigClass.c,
            bigClass.d, bigClass.e, bigClass.f,
            bigClass.g, bigClass.h,
            BigClassPart1.unapply(bigClass.bigClassPart1).get,
            BigClassPart2.unapply(bigClass.bigClassPart2).get,
          ))
      }
    )
  }

  object BigClassTable {
    val all = TableQuery[BigClassTable]
  }

}