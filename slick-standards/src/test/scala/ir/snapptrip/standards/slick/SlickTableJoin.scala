package ir.snapptrip.standards.slick

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite

class SlickTableJoin extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  test("Simple definition of foreign-key and easy table-joining functionality") {
    def getUser(userId: Long) = for {
      user    <- UserTable.all.filter(_.id === userId)
      address <- user.address /** join user + address **/
      city    <- address.city /** join address + city **/
    } yield {
      (user, address, city)
    }
    autoRollback(
      for {
        _                                 <- CityTable.all.schema.create
        _                                 <- AddressTable.all.schema.create
        _                                 <- UserTable.all.schema.create
        tehranId                          <- CityTable.insert += City(None, "Tehran", "Iran")
        nimaAddressId                     <- AddressTable.insert += Address(None, 22, "Dabestan", tehranId)
        nimaId                            <- UserTable.insert += User(None, "Nima", 29, nimaAddressId)
        (nimaUser, nimaAddress, nimaCity) <- getUser(nimaId).result.head
      } yield {
        assert(nimaUser.firstName == "Nima")
        assert(nimaUser.age == 29)
        assert(nimaAddress.number == 22)
        assert(nimaAddress.street == "Dabestan")
        assert(nimaCity.name == "Tehran")
        assert(nimaCity.country == "Iran")
      }
    )
  }

  // -----------------------------------------------------------------------------------------------------------------
  // MODELS ----------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------

  case class User(id: Option[Long], firstName: String, age: Int, addressId: Long)
  case class Address(id: Option[Long], number: Int, street: String, cityId: Long)
  case class City(id: Option[Long], name: String, country: String)

  // -----------------------------------------------------------------------------------------------------------------
  // MODEL-TABLE MAPPING ---------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------

  class UserTable(tag: Tag) extends Table[User](tag, "UserTable") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc).?
    val firstName = column[String]("first_name")
    val age = column[Int]("age")
    val addressId = column[Long]("address_id")
    /** define foreign-key for easy joining **/
    def address = foreignKey("user_address_fk", addressId, AddressTable.all)(_.id.get)
    override def * = (id, firstName, age, addressId) <> (User.tupled, User.unapply)
  }

  object UserTable {
    val all = TableQuery[UserTable]
    val insert = all.returning(all.map(_.id.get))
  }

  class AddressTable(tag: Tag) extends Table[Address](tag, "AddressTable") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc).?
    val number = column[Int]("number")
    val street = column[String]("street")
    val cityId = column[Long]("city_id")
    /** define foreign-key for easy joining **/
    def city = foreignKey("address_city_fk", cityId, CityTable.all)(_.id.get)
    override def * = (id, number, street, cityId) <> (Address.tupled, Address.unapply)
  }

  object AddressTable {
    val all = TableQuery[AddressTable]
    val insert = all.returning(all.map(_.id.get))
  }

  class CityTable(tag: Tag) extends Table[City](tag, "CityTable") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc).?
    val name = column[String]("name")
    val country = column[String]("country")
    override def * = (id, name, country) <> (City.tupled, City.unapply)
  }

  object CityTable {
    val all = TableQuery[CityTable]
    val insert = all.returning(all.map(_.id.get))
  }

}
