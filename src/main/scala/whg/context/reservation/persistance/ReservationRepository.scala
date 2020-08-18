package whg.context.reservation.persistance

import java.time.Instant
import java.util.Date

import cats.effect.IO
//import cats.implicits.catsSyntaxApplicativeId
import cats.implicits._
import doobie.ConnectionIO
import whg.common.database.DatabaseDriver
import whg.common.threadpool.ThreadPools
import whg.context.reservation.domian._

import doobie.implicits._

class ReservationRepository(postgresDriver: DatabaseDriver) {
  import postgresDriver._
  import ctx._

  private implicit val encodeInstant: MappedEncoding[Instant, Date] = MappedEncoding[Instant, Date](Date.from)
  private implicit val decodeInstant: MappedEncoding[Date, Instant] = MappedEncoding[Date, Instant](_.toInstant)

  /**
   * creates new reservation if max number of ticket per event is not exceeded.
   * to make reservations atomic with max reserved ticket number check we have an extra table reservation_counters
   */

  def emptyQuery: ConnectionIO[Unit] = ().pure[ConnectionIO]

  def insertWithMaxReservationCheck(reservation: Reservation): IO[Long] = {
    val queries = for {
      affected <- updateWithCounterIncrementQuery(reservation.eventId, reservation.ticketCount)
      _        <- if(affected > 0) ctx.run(query[Reservation].insert(lift(reservation)).returningGenerated(_.id)) else emptyQuery
    } yield affected
    queries.transact(xa)
  }

  def remove(reservationId: Long): IO[Long] = {
    val queries = for {
      reservationToCancel <- ctx.run(query[Reservation].filter(r => r.id == lift(reservationId))).map(reservations => reservations.head)
      affected            <- updateWithCounterIncrementQuery(reservationToCancel.eventId, -reservationToCancel.ticketCount)
      _                   <- ctx.run(query[Reservation].filter(r => r.id == lift(reservationId)).delete)
    } yield affected
    queries.transact(xa)
  }

  def updateReservationExpiryDate(reservationId: Long, newExpiryDate: Instant): IO[Long] =
    ctx.run(query[Reservation].filter(r => r.id == lift(reservationId)).update(r => r.expiryDate -> lift(newExpiryDate))).transact(xa)

  def findAllReservations(): IO[List[Reservation]] =
    ctx.run(query[Reservation]).transact(xa)

  def findAllReservationsForEvent(eventId: Long): IO[List[Reservation]] =
    ctx.run(query[Reservation].filter(r => r.eventId == lift(eventId))).transact(xa)

  def findReservationsForClient(eventId: Long, clientId: Long): IO[List[Reservation]] =
    ctx.run(query[Reservation].filter(r => r.clientId == lift(clientId) && r.eventId == lift(eventId))).transact(xa)

  def findReservationCounter(eventId: Long): IO[Option[ReservationCounter]] =
    ctx.run(query[ReservationCounter].filter(rc => rc.eventId == lift(eventId))).map(_.headOption).transact(xa)

  private def updateWithCounterIncrementQuery(eventId: Long, ticketsToReserve: Long): ConnectionIO[Long] = {
    ctx.run(
      quote{
        query[ReservationCounter]
          .filter(rc => rc.eventId == lift(eventId) && rc.maxTickets >= rc.reservedTickets + lift(ticketsToReserve))
          .update(rc => rc.reservedTickets -> (rc.reservedTickets + lift(ticketsToReserve)))
      }
    )
  }

}
