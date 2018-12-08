package ir.snapptrip.standards.sql

import java.util.concurrent.Executors

import ir.snapptrip.standards.{DatabaseConnector, DatabaseTests}
import org.scalatest.FunSuite

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class PlsqlAtomicReadAndUpdate extends FunSuite with DatabaseConnector with DatabaseTests {

  import api._

  override implicit val ec: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(30))

  type PickId = Long
  type VoucherId = Long

  test("A simple of an plsql-function that does atomic read-then-update") {
    insertVouchersThenPick(PickFreeVoucherFn.create)
  }

  test("Same test except this is non-atomic and should fail") {
    assertThrows[VoucherMultipleAllocationException] {
      insertVouchersThenPick(PickFreeVoucherFn.create.replace("FOR UPDATE;", ";")) // remove "FOR UPDATE")
    }
  }

  def insertVouchersThenPick(pickerPlsqlFunction: String) = {
    // insert sample vouchers
    database.run(
      (for {
        _ <- VoucherTable.safeDrop
        _ <- VoucherTable.all.schema.create // create 100 vouchers (randomly used/free)
        _ <- VoucherTable.all ++= (1 to 100).map(idx => Voucher(None, Random.nextBoolean()))
        _ <- sqlu"""#$pickerPlsqlFunction"""
      } yield {
      }).transactionally
    ).await

    try {
      // pick first 20 free vouchers in parallel
      val picks = Future.sequence(
          (1 to 25)
            .map(pickId => (pickId.asInstanceOf[PickId], PickFreeVoucherFn()).result)
            .map(database.run(_))
        ).await
      picks
        .groupBy { case (pickId,    voucherId) => voucherId }
        .find    { case (voucherId,     picks) => picks.length > 1 }
        .foreach { case (voucherId,     picks) => throw VoucherMultipleAllocationException(voucherId, picks.map(_._1)) }
    } finally {
      // clean-up
      database.run(VoucherTable.safeDrop).await
    }
  }

  // -----------------------------------------------------------------------------------------------------------------
  // MODELS ----------------------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------

  case class Voucher(id: Option[VoucherId] = None, used: Boolean)

  case class VoucherMultipleAllocationException(voucher: VoucherId, picks: Seq[PickId])
    extends Exception(s"Multiple voucher picks ($picks) got the same voucher $voucher !")

  // -----------------------------------------------------------------------------------------------------------------
  // MODEL-TABLE MAPPING ---------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------

  class VoucherTable(tag: Tag) extends Table[Voucher](tag, "VoucherTable") {
    val id = column[Option[VoucherId]]("id", O.PrimaryKey, O.AutoInc)
    val state = column[Boolean]("state")
    override def * = (id, state) <> (Voucher.tupled, Voucher.unapply)
  }

  object VoucherTable {
    val all = TableQuery[VoucherTable]
    def safeDrop = sqlu"""DROP TABLE IF EXISTS "VoucherTable""""
  }

  // -----------------------------------------------------------------------------------------------------------------
  // ATOMIC READ-N-UPDATE FUNCTION -----------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------------------------

  object PickFreeVoucherFn {
    def apply(): Rep[VoucherId] = {
      SimpleFunction.nullary("pick_free_voucher")
    }
    val create =
      """
        CREATE OR REPLACE FUNCTION pick_free_voucher() RETURNS BIGINT AS '
          DECLARE voucher_id BIGINT;
        BEGIN
          SELECT "id" from "VoucherTable"
            WHERE "state" = false
            ORDER BY "id" ASC
            LIMIT 1
            INTO voucher_id
            FOR UPDATE; --------- THE KEY TO MAKE THIS TRANSACTIONAL
          UPDATE "VoucherTable"
            SET "state" = true
            WHERE "id" = voucher_id;
          RETURN voucher_id;
        END;
        ' LANGUAGE plpgsql;
      """
  }

}
