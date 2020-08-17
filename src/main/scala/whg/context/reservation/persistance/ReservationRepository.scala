package whg.context.reservation.persistance

import java.time.Instant
import java.util.Date

import akka.Done
import cats.effect.IO
import cats.free.Free
import cats.implicits.catsSyntaxApplicativeId
import cats.implicits._
import doobie.ConnectionIO
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

  def emptyQuery(): ConnectionIO[Unit] = ().pure[ConnectionIO]

  def insertWithMaxReservationCheck_experimental(reservation: Reservation): IO[Long] = {
    val q1 = quote {
      query[ReservationCounter]
        .filter(rc => rc.eventId == lift(reservation.eventId) && rc.maxTickets >= rc.reservedTickets + lift(reservation.ticketCount))
        .update(rc => rc.reservedTickets -> (rc.reservedTickets + lift(reservation.ticketCount)))
    }

    val q2 = quote {
      query[Reservation].insert(lift(reservation)).returningGenerated(_.id)
    }
    doobieCtx.run(q1).transact(xa)

    val queries =
      for {
        a <- doobieCtx.run(q1)
        _ <- doobieCtx.run(q2)
      } yield a


    queries.transact(xa)
  }

  def insertWithMaxReservationCheck(reservation: Reservation): IO[Long] = {
    val queries = for {
      affected <- updateWithCounterIncrementQuery(reservation.eventId, reservation.ticketCount)
      _        <- if(affected > 0) doobieCtx.run(query[Reservation].insert(lift(reservation)).returningGenerated(_.id)) else emptyQuery()
    } yield affected
    queries.transact(xa)
  }



  def remove(reservationId: Long): IO[Long] = {
    val queries = for {
      reservationToCancel <- doobieCtx.run(query[Reservation].filter(r => r.id == lift(reservationId))).map(reservations => reservations.head)
      affected            <- updateWithCounterIncrementQuery(reservationToCancel.eventId, -reservationToCancel.ticketCount)
      _                   <- doobieCtx.run(query[Reservation].filter(r => r.id == lift(reservationId)).delete)
    } yield affected
    queries.transact(xa)
  }

  def updateReservationExpiryDate(reservationId: Long, newExpiryDate: Instant): IO[Long] =
    doobieCtx.run(query[Reservation].filter(r => r.id == lift(reservationId)).update(r => r.expiryDate -> lift(newExpiryDate))).transact(xa)

  def findAllReservations(): IO[List[Reservation]] =
    doobieCtx.run(query[Reservation]).transact(xa)

  def findAllReservationsForEvent(eventId: Long): IO[List[Reservation]] =
    doobieCtx.run(query[Reservation].filter(r => r.eventId == lift(eventId))).transact(xa)

  def findReservationsForClient(eventId: Long, clientId: Long): IO[List[Reservation]] =
    doobieCtx.run(query[Reservation].filter(r => r.clientId == lift(clientId) && r.eventId == lift(eventId))).transact(xa)

  def findReservationCounter(eventId: Long): IO[Option[ReservationCounter]] =
    doobieCtx.run(query[ReservationCounter].filter(rc => rc.eventId == lift(eventId))).map(_.headOption).transact(xa)

  private def updateWithCounterIncrementQuery(eventId: Long, ticketsToReserve: Long): ConnectionIO[Long] = {
    doobieCtx.run(
      quote{
        query[ReservationCounter]
          .filter(rc => rc.eventId == lift(eventId) && rc.maxTickets >= rc.reservedTickets + lift(ticketsToReserve))
          .update(rc => rc.reservedTickets -> (rc.reservedTickets + lift(ticketsToReserve)))
      }
    )
  }

}
