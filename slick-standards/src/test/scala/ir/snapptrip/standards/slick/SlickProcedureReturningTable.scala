package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite

class SlickProcedureReturningTable extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("Function returning a single value run (all arguments entered)") {
    import SampleFunctionReturningTableFn.SampleRow
    autoRollback(
      for {
        _      <- sqlu"#${SampleFunctionReturningTableFn.create}"
        result <- SampleFunctionReturningTableFn(3L, "Item")
      } yield {
        assert(result == Seq(SampleRow(1L, "Item1"), SampleRow(2L, "Item2"), SampleRow(3L, "Item3")))
      }
    )
  }

  object SampleFunctionReturningTableFn {

    import slick.jdbc.GetResult
    import GetResult._

    def apply(variable_long: Long, variable_string: String) = {
      implicit val rowResultGet = GetResult[(Long, String)].andThen(SampleRow.tupled)
      sql"""select invocation.number, invocation.name from sample_function_returning_table($variable_long, $variable_string) invocation""".as[SampleRow]
    }

    case class SampleRow(number: Long, name: String)

    val create =
      """
        create or replace
          function sample_function_returning_table(
            variable_long bigint,
            variable_string character varying
          )
          returns TABLE(number bigint, name character varying)
          language plpgsql
        as $$
        begin
          return query (
            select
              i as number,
              concat(variable_string, i)::character varying as name
            FROM
              generate_series(1, variable_long) i
          );
        end
        $$;
      """

  }

}