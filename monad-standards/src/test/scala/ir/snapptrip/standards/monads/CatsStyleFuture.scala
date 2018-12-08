package ir.snapptrip.standards.monads

import cats.data.EitherT
import cats.implicits._
import ir.snapptrip.standards.monads.CatsStyleToolbox._
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions
import scala.util.Success

class CatsStyleFuture1 extends FunSuite with SampleMonads {

  import CatsStyleToolbox._

  def result: Future[Either[Error, String]] = {
    (for {
      step1 <- EitherT.right[Error](rSuccessString)
      step2 <- EitherT.right[Error](rFailure.opt)
      step3 <- EitherT.fromOption[Future](step2, "ERROR0")
      step4 <- EitherT.fromEither[Future](rRight)
      step6 <- EitherT.apply(rSuccessRight)
      step7 <- EitherT.right[Error](rSuccessInt)
      step8 <- EitherT.fromOptionF(rSuccessOptionInt, "ERROR1")
      step9 <- EitherT.fromOption[Future](rOptionInt, "ERROR2")
    } yield {
      "Finished"
    }).value
  }

  test("Working with different monads in a single for-comprehension using cats' EitherT directly") {
    Await.ready(result, Duration.Inf).value.get match {
      case Success(Left(error))  => Console.err.println(s"Result Error: $error")
      case Success(Right(value)) => println(s"Result: $value")
    }
  }

}

class CatsStyleFuture2 extends FunSuite with SampleMonads {

  def result: Future[Either[Error, String]] = {
    (for {
      step1 <- <<(rSuccessString)
      step2 <- <<(rFailure.opt)
      step3 <- <<(step2, "ERROR0").ensure("""step2 did not contain "SOMETHING" !""")(_.contains("SOMETHING"))
      step4 <- <<(rRight)
      step6 <- <<<(rSuccessRight)
      step7 <- <<(rSuccessInt)
      step8 <- <<<(rSuccessOptionInt, "ERROR1")
      step9 <- <<(rOptionInt, "ERROR2")
    } yield {
      "Finished"
    }).value
  }

  test("Working with different monads in a single for comprehension using cats' EitherT with a symbolic convertor") {
    Await.ready(result, Duration.Inf).value.get match {
      case Success(Left(error))  => Console.err.println(s"Result Error: $error")
      case Success(Right(value)) => println(s"Result: $value")
    }
  }

}