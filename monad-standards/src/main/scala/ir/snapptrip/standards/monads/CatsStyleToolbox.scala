package ir.snapptrip.standards.monads

import cats.data._
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

object CatsStyleToolbox extends CommonValidation {

  type Error = String

  object <<< {
    def apply[A](i: Future[Either[Error, A]]): EitherT[Future, Error, A] = EitherT.apply(i)
    def apply[A](i: Future[Option[A]], error: => Error): EitherT[Future, Error, A] = EitherT.fromOptionF(i, error)
  }

  object << {
    def apply[A](i: Future[A]): EitherT[Future, Error, A] = EitherT.right[Error](i)
    def apply[A](i: Either[Error, A]): EitherT[Future, Error, A] = EitherT.fromEither(i)
    def apply[A](i: Option[A], error: => Error): EitherT[Future, Error, A] = EitherT.fromOption(i, error)
  }

}
