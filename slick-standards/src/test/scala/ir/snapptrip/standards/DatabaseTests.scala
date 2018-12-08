package ir.snapptrip.standards

import com.vividsolutions.jts.geom.{Coordinate, GeometryFactory, Point}
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.language.implicitConversions
import scala.util.{Failure, Success}

trait DatabaseTests extends OtherTools { databaseConnector: DatabaseConnector =>

  import api._

  def autoRollback[R, E <: Effect](action: DBIOAction[R, NoStream, E]): R =
  {
    val result = Promise[R]()
    val noEffectAction =
      for {
        r <- action
      } yield {
        result.success(r)
        throw TransactionCompleted
      }
    val transaction = database.run(noEffectAction.transactionally)
    transaction.transformWith {
      case Failure(TransactionCompleted) =>
        result.future
      case Failure(e) =>
        Future.failed(e)
      case Success(value) =>
        Future.failed(new IllegalStateException(s"Transaction did not finish successfully, returned: $value"))
    }(ec).await
  }

  case object TransactionCompleted extends Exception

}

trait OtherTools {

  implicit class RichFuture[T](future: Future[T]) {
    def await: T = Await.result(future, Duration.Inf)
  }

  def gisCoordinate(longitude: Double, latitude: Double): Point = {
    val geometryFactory = new GeometryFactory()
    new Point(new CoordinateArraySequence(Array(new Coordinate(longitude, latitude))), geometryFactory)
  }

}
