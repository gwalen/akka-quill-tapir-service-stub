package whg.context.reservation.domian.dto

import java.time.Instant

import whg.context.reservation.domian.Reservation

case class ReservationDto(
//  id: Option[Long],
  clientId: Long,
  eventId: Long,
  ticketCount: Int,
//  expiryDate: Option[Instant]
)

object ReservationDto {
  def from(r: Reservation): ReservationDto = ReservationDto(r.clientId, r.eventId, r.ticketCount)
}
