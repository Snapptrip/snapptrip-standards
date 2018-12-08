package ir.snapptrip.standards.monads

import org.scalatest.FunSuite

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success}

class ImperativeStyle extends FunSuite with SampleMonads {

  import ImperativeStyleToolbox._

  def result: Future[String] = async {
    val step1  = await(rSuccessString)
    val step2  = await(rFailure.opt)
    val step3  = unlift(step2, "The reason for error in step 3 !")
    val step4  = unlift(rRight, "The reason for error in step 4 !")
                 validate(rRight.contains("Three"), "Extra validation failure after step 4 !")
    val step5  = await(rSuccessRight)
    val step6  = unlift(step5, (err: Error) => s"The reason for error in step 6: $err")
    val step7  = await(rSuccessInt)
    val step8  = await(rSuccessOptionInt)
    val step9  = unlift(step8, "The reason for error in step 9 !")
    val step10 = unlift(rOptionInt, "The reason for error in step 10 !")
    "Finished"
  }

  test("Imperative style unfolding of different types of monads") {
    Await.ready(result, Duration.Inf).value.get match {
      case Failure(exception) => exception.printStackTrace()
      case Success(value) => println(s"Result: $value")
    }
  }

}

