package whg.context.reservation.domian.dto

import java.time.Instant

case class ReservationExtendRequest(reservationId: Long, newExpiryDate: Instant)
