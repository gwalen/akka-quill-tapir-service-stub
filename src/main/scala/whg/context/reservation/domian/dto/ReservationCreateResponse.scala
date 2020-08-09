package whg.context.reservation.domian.dto

sealed trait ReservationCreateResponse

object ReservationCreateResponses {
  case object Successful                          extends ReservationCreateResponse
  case object NotEnoughTickets                    extends ReservationCreateResponse
  case object TooManyTicketsForClient             extends ReservationCreateResponse
  case object EventReservationsNotFound           extends ReservationCreateResponse
  case object ClientAlreadyHasReservationForEvent extends ReservationCreateResponse
}
