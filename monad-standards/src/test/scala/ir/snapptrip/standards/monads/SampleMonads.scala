package ir.snapptrip.standards.monads

import ir.snapptrip.standards.monads.CatsStyleToolbox.Error

import scala.concurrent.Future

trait SampleMonads extends CommonValidation {

  import Future._

  val rSuccessString: Future[String] = successful("One")
  val rFailure: Future[String] = failed(ValidationException("Two"))
  val rRight: Either[Error, String] = Right("Three")
  val rSuccessRight: Future[Either[Error, Int]] = successful(Right(1))
  val rSuccessInt: Future[Int] = successful(1)
  val rSuccessOptionInt: Future[Option[Int]] = successful(Some(1))
  val rOptionInt: Option[Int] = Some(1)

}
