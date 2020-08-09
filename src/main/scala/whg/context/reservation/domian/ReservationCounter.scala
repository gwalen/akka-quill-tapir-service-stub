package whg.context.reservation.domian

case class ReservationCounter(eventId: Long, maxTickets: Long, reservedTickets: Long, maxTicketsPerClient: Long)
