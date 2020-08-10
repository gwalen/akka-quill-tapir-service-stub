package whg.context.reservation.persistance

import java.time.Instant
import java.util.Date

import whg.common.database.PostgresDriver
import whg.context.reservation.domian._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ReservationRepository(postgresDriver: PostgresDriver)(implicit ec: ExecutionContext) {
  import postgresDriver._
  import ctx._

  private implicit val encodeInstant: MappedEncoding[Instant, Date] = MappedEncoding[Instant, Date](Date.from)
  private implicit val decodeInstant: MappedEncoding[Date, Instant] = MappedEncoding[Date, Instant](_.toInstant)

  /**
   * creates new reservation if max number of ticket per event is not exceeded.
   * to make reservations atomic with max reserved ticket number check we have an extra table reservation_counters
   */
  def insertWithMaxReservationCheck(reservation: Reservation): Future[Long] = {
    val queries = for {
      affected <- updateWithCounterIncrementQuery(reservation.eventId, reservation.ticketCount)
      _        <- if(affected > 0) ctx.runIO(query[Reservation].insert(lift(reservation)).returningGenerated(_.id)) else IO.successful()
    } yield affected
    performIO(queries.transactional)
  }

  def remove(reservationId: Long): Future[Long] = {
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

  private def updateWithCounterIncrementQuery(eventId: Long, ticketsToReserve: Long): IO[Long, Effect.Write] = {
    ctx.runIO(
      query[ReservationCounter]
        .filter(rc => rc.eventId == lift(eventId) && rc.maxTickets >= rc.reservedTickets + lift(ticketsToReserve))
        .update(rc => rc.reservedTickets -> (rc.reservedTickets + lift(ticketsToReserve)))
    )
  }

}
