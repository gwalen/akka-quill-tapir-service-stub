package whg.context.reservation.domian

import java.time.Instant
import java.time.temporal.ChronoUnit

import whg.context.reservation.domian.dto.ReservationDto

case class Reservation(
  id: Long,
  clientId: Long,
  eventId: Long,
  ticketCount: Int,
  expiryDate: Instant
)

object Reservation {
  def from(r: ReservationDto): Reservation =
    Reservation(0L, r.clientId, r.eventId, r.ticketCount, Instant.now().plus(1, ChronoUnit.DAYS))
}

