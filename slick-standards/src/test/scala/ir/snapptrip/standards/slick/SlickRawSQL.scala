package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite

class SlickRawSQL extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("A simple query using slick's raw-sql feature") {
    autoRollback(
      for {
        result <- sql"""SELECT 1000""".as[Int].headOption
      } yield {
        assert(result.contains(1000))
      }
    )
  }

}
