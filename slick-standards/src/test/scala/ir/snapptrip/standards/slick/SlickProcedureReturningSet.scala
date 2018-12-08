package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite

class SlickProcedureReturningSet extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("Function returning a single value run (all arguments entered)") {
    autoRollback(
      for {
        _      <- sqlu"#${SampleFunctionReturningSetFn.create}"
        result <- SampleFunctionReturningSetFn(3L, "Item")
      } yield {
        assert(result == Seq("Item1", "Item2", "Item3"))
      }
    )
  }

  object SampleFunctionReturningSetFn {

    def apply(variable_long: Long, variable_string: String) =
      sql"""select invocation.* from sample_function_returning_set($variable_long, $variable_string) invocation""".as[String]

    val create =
      """
        create or replace
          function sample_function_returning_set(
            variable_long bigint,
            variable_string character varying
          )
          returns SETOF character varying
          language plpgsql
        as $$
        begin
          return query (
            select
              concat(variable_string, i)::character varying as name
            FROM
              generate_series(1, variable_long) i
          );
        end
        $$;
      """

  }

}