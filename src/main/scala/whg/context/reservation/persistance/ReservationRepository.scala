package whg.context.reservation.persistance

import java.time.Instant
import java.util.Date

import akka.Done
import cats.free.Free
import doobie.free.connection
import io.getquill.Action
import io.getquill.Update
import whg.common.database.DatabaseDriver
import whg.common.threadpool.ThreadPools
import whg.context.reservation.domian._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import doobie.implicits._

class ReservationRepository(postgresDriver: DatabaseDriver, threadPools: ThreadPools) {
  import postgresDriver._
//  import ctx._
  import doobieCtx._
  import threadPools.jdbcEc

  private implicit val encodeInstant: MappedEncoding[Instant, Date] = MappedEncoding[Instant, Date](Date.from)
  private implicit val decodeInstant: MappedEncoding[Date, Instant] = MappedEncoding[Date, Instant](_.toInstant)

  /**
   * creates new reservation if max number of ticket per event is not exceeded.
   * to make reservations atomic with max reserved ticket number check we have an extra table reservation_counters
   */

  def emptyQuery() = query[Reservation]

  def insertWithMaxReservationCheck(reservation: Reservation): Long = {
//    val queries = quote {
//      for {
//        a <- query[ReservationCounter]
//              .filter(rc => rc.eventId == lift(reservation.eventId) && rc.maxTickets >= rc.reservedTickets + lift(reservation.ticketCount))
//              .update(rc => rc.reservedTickets -> (rc.reservedTickets + lift(reservation.ticketCount)))
//        _ <- query[Reservation].insert(lift(reservation)).returningGenerated(_.id)
//      } yield a
//    }

    val q1 = quote {
      query[ReservationCounter]
        .filter(rc => rc.eventId == lift(reservation.eventId) && rc.maxTickets >= rc.reservedTickets + lift(reservation.ticketCount))
        .update(rc => rc.reservedTickets -> (rc.reservedTickets + lift(reservation.ticketCount)))
    }

    val q2 = quote {
      query[Reservation].insert(lift(reservation)).returningGenerated(_.id)
    }

    doobieCtx.run(q1).transact(xa)


    val queries: Free[connection.ConnectionOp, Long] =
      for {
        a <- doobieCtx.run(q1)
        _ <- doobieCtx.run(q2)
      } yield a


    queries.transact(xa)
  }

  def insertWithMaxReservationCheck(reservation: Reservation): Long = {
    val queries = for {
      affected <- updateWithCounterIncrementQuery2(reservation.eventId, reservation.ticketCount)
      _        <- if(affected > 0) query[Reservation].insert(lift(reservation)).returningGenerated(_.id) else emptyQuery()
    } yield affected
    doobieCtx(queries).transac(xa)
  }



  def remove(reservationId: Long): Long = {
    val aa = ctx.runIO(query[Reservation].filter(r => r.id == lift(reservationId))).map(reservations => reservations.head)
    val queries = for {
      reservationToCancel <- ctx.runIO(query[Reservation].filter(r => r.id == lift(reservationId))).map(reservations => reservations.head)
      affected            <- updateWithCounterIncrementQuery(reservationToCancel.eventId, -reservationToCancel.ticketCount)
      _                   <- ctx.runIO(query[Reservation].filter(r => r.id == lift(reservationId)).delete)
    } yield affected
    performIO(queries.transactional)
  }

  def updateReservationExpiryDate(reservationId: Long, newExpiryDate: Instant): Future[Long] =
    ctx.run(query[Reservation].filter(r => r.id == lift(reservationId)).update(r => r.expiryDate -> lift(newExpiryDate)))

  def findAllReservations(): Future[List[Reservation]] =
    ctx.run(query[Reservation])

  def findAllReservationsForEvent(eventId: Long): Future[List[Reservation]] =
    ctx.run(query[Reservation].filter(r => r.eventId == lift(eventId)))

  def findReservationsForClient(eventId: Long, clientId: Long): Future[List[Reservation]] =
    ctx.run(query[Reservation].filter(r => r.clientId == lift(clientId) && r.eventId == lift(eventId)))

  def findReservationCounter(eventId: Long): Future[Option[ReservationCounter]] =
    ctx.run(query[ReservationCounter].filter(rc => rc.eventId == lift(eventId))).map(_.headOption)

  private def updateWithCounterIncrementQuery(eventId: Long, ticketsToReserve: Long): Update[ReservationCounter] = {
    query[ReservationCounter]
      .filter(rc => rc.eventId == lift(eventId) && rc.maxTickets >= rc.reservedTickets + lift(ticketsToReserve))
      .update(rc => rc.reservedTickets -> (rc.reservedTickets + lift(ticketsToReserve)))
  }


  private def updateWithCounterIncrementQuery2(eventId: Long, ticketsToReserve: Long): doobie.ConnectionIO[Long] = {
    doobieCtx.run(
      quote{
      query[ReservationCounter]
        .filter(rc => rc.eventId == lift(eventId) && rc.maxTickets >= rc.reservedTickets + lift(ticketsToReserve))
        .update(rc => rc.reservedTickets -> (rc.reservedTickets + lift(ticketsToReserve)))}
    )
  }

}
