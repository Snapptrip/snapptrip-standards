package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Month}

import org.scalatest.FunSuite

class SlickProcedureReturningValue extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("Function returning a single value run (all arguments entered)") {
    autoRollback(
      for {
        _      <- sqlu"#${SampleFunctionFn.create}"
        result <- SampleFunctionFn(1001001L, "Hello", Some(LocalDate.of(1999, Month.MARCH, 5))).result
      } yield {
        assert(result == "1001001|Hello|1999-03-05")
      }
    )
  }

  test("Function returning a single value run (with default argument)") {
    autoRollback(
      for {
        _      <- sqlu"#${SampleFunctionFn.create}"
        now     = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        result <- SampleFunctionFn(1001001L, "Hello").result
      } yield {
        assert(result == s"1001001|Hello|$now")
      }
    )
  }

  object SampleFunctionFn {

    def apply(variable_long: Rep[Long], variable_string: Rep[String], variable_date: Rep[Option[LocalDate]] = None): Rep[String] = {
      // this way we will always have 3 arguments but if we want postgres to apply default-values for tailing null-parameters, we should cut the arg-seq tailing nulls
      val none = None: Rep[Option[Int]]
      val arguments = Seq(variable_long, variable_string, variable_date).reverse.dropWhile(_ == none).reverse
      SimpleFunction[String]("sample_function").apply(arguments)
    }

    val create =
      """
        create or replace function sample_function(
          variable_long bigint,
          variable_string character varying default 'default-variable-string',
          variable_date date default ('now'::text)::date
        )
          returns character varying
          language plpgsql
        as $$
        begin
          return concat(variable_long::text, '|', variable_string, '|', variable_date::text);
        end
        $$;
      """

  }

}
