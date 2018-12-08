package ir.snapptrip.standards

import com.github.tminglei.slickpg._
import com.github.tminglei.slickpg.utils._
import javax.sql.DataSource
import org.postgresql.ds.PGSimpleDataSource
import _root_.slick.jdbc.JdbcBackend
import _root_.slick.jdbc.JdbcBackend.DatabaseDef

import scala.concurrent.ExecutionContext

trait DatabaseConnector {
  val api = ExtendedPostgresProfile.api
  implicit val ec = ExecutionContext.Implicits.global
  lazy val dataSource: DataSource = DatabaseConnector.dataSource
  lazy val database: DatabaseDef = DatabaseConnector.database
}

object DatabaseConnector {
  lazy val dataSource = {
    val src = new PGSimpleDataSource()
    src.setServerName("127.0.0.1")
    src.setUser("postgres")
    src.setPassword("127.0.0.1")
    src.setDatabaseName("postgres")
    src.setCurrentSchema("public")
    src.setPortNumber(33055)
    src
  }
  private lazy val database = JdbcBackend.Database.forDataSource(dataSource, None)
}

trait ExtendedPostgresProfile
  extends ExPostgresProfile
    with PgDate2Support
    with PgArraySupport
    with PgCompositeSupport
    with PgPostGISSupport
    with PgCirceJsonSupport
    with PgCommonJdbcTypes
{
  override def pgjson: String = "jsonb"
  override val api: API = new API {}
  trait API
    extends super.API
      with PostGISImplicits
      with PostGISAssistants
      with ArrayImplicits
      with DateTimeImplicits
      with JsonImplicits
      with CirceJsonPlainImplicits
      with Date2DateTimePlainImplicits
      with PostGISPlainImplicits
      with SimpleArrayPlainImplicits

}

object ExtendedPostgresProfile extends ExtendedPostgresProfile