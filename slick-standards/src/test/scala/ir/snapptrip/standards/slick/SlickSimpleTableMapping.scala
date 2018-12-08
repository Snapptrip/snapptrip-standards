package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite

class SlickSimpleTableMapping extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("A simple table creation-insertion-query") {
    autoRollback(
      for {
        _     <- UserTable.all.schema.create
        _     <- UserTable.all ++= Seq(User(name = "Nima Taheri"), User(name = "Hassan Monfared"))
        names <- UserTable.all.map(_.name).result
      } yield {
        assert(names == Seq("Nima Taheri", "Hassan Monfared"))
      }
    )
  }

  // MODELS ----------------------------------------------------------------------------------------------------------

  case class User(id: Option[Long] = None, name: String)

  // MODEL-TABLE MAPPING ---------------------------------------------------------------------------------------------

  class UserTable(tag: Tag) extends Table[User](tag, "UserTable") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc).?
    val name = column[String]("name")
    override def * = (id, name) <> (User.tupled, User.unapply)
  }

  object UserTable {
    val all = TableQuery[UserTable]
  }

}