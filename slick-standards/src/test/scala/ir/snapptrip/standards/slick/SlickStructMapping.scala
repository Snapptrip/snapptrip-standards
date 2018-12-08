package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests, ExtendedPostgresProfile}
import com.github.tminglei.slickpg.composite.Struct
import org.scalatest.FunSuite

class SampleWithStruct extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("Simple mapping for composite-types defined in postgres") {
    autoRollback(
      for {
        _      <- StudentCompositeSlick.drop
        _      <- StudentCompositeSlick.create
        sample <- StudentCompositeSlick.sample.head
        _      <- StudentCompositeSlick.drop
      } yield {
        assert(sample == StudentComposite(1, "Nima Taheri"))
      }
    )
  }

  // -----------------------------------------------------------------------------------------------------------------
  // MODEL-TABLE MAPPING ---------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------

  object StudentCompositeSlick {
    import slick.jdbc.GetResult
    import ExtendedPostgresProfile._

    implicit val mapper = createCompositeJdbcType[StudentComposite]("student")
    implicit val getresult = GetResult[StudentComposite](r => nextComposite[StudentComposite](r).get)

    val create = sqlu"""
      create type student as (
      	id bigint,
      	name varchar(100)
      );
    """

    val drop = sqlu"""
      drop type if exists student;
    """

    val sample = sql"""
      select (1, 'Nima Taheri')::student;
    """.as[StudentComposite]

  }

}

// -----------------------------------------------------------------------------------------------------------------
// MODELS ----------------------------------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------------------------------------

// warning: this could not be an inner-class (limitation of pg-slick)
case class StudentComposite(
  id: Long,
  name: String
) extends Struct