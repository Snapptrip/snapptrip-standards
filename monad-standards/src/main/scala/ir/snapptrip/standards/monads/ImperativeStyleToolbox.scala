package ir.snapptrip.standards.monads

object ImperativeStyleToolbox extends CommonValidation{

  type Error = String

  def unlift[A, B](either: Either[A, B], ferror: => String): B = {
    unlift(either, (_: A) => ferror)
  }

  def unlift[A, B](either: Either[A, B], ferror: A => String): B = {
    validate(either.isRight, ferror(either.left.get))
    either.right.get
  }

  def unlift[A](opt: Option[A], ferror: => String): A = {
    validate(opt.isDefined, ferror)
    opt.get
  }

}
