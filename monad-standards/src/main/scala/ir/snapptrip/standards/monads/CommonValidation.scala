package ir.snapptrip.standards.monads

import ir.snapptrip.standards.monads.ImperativeStyleToolbox.Error

import scala.concurrent.{ExecutionContext, Future}

trait CommonValidation {

  implicit class RichFuture[T](f: Future[T])(implicit ec: ExecutionContext) {
    def opt: Future[Option[T]] = f.map(Some(_)).recover { case _ => None }
    def either: Future[Either[String, T]] = f.map(Right(_)).recover { case exp: Exception => Left(exp.getMessage) }
  }

  def validate(check: Boolean, message: => String) {
    if (!check)
      throw ValidationException(message)
  }

  def isolate[T](body: => T): Either[Error, T] = {
    try {
      Right(body)
    } catch {
      case ValidationException(msg) => Left(msg)
    }
  }

  case class ValidationException(msg: String) extends Exception(msg)

}
