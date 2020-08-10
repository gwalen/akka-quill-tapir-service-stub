package whg.context.reservation.restapi

import akka.Done
import whg.context.reservation.domian.Reservation
import whg.context.reservation.domian.dto.ReservationCreateRequest
import whg.context.reservation.domian.dto.ReservationExtendRequest
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.json.circe._

object ReservationEndpoints {

  private val tag = "reservations"

  val baseEndpoint: Endpoint[Unit, String, Unit, Nothing] = endpoint.errorOut(stringBody).in("api").in("reservations")

  val createReservation: Endpoint[ReservationCreateRequest, String, String, Nothing] = baseEndpoint.post
    .in(jsonBody[ReservationCreateRequest])
    .out(jsonBody[String])
    .tag(tag)

  val extendReservation: Endpoint[ReservationExtendRequest, String, Done, Nothing] = baseEndpoint.patch
    .in(jsonBody[ReservationExtendRequest])
    .out(jsonBody[Done])
    .tag(tag)

  val cancelReservation: Endpoint[Long, String, Done, Nothing] = baseEndpoint.delete
    .in(path[Long]("reservationId"))
    .out(jsonBody[Done])
    .tag(tag)

  val findReservations: Endpoint[Unit, String, List[Reservation], Nothing] = baseEndpoint.get
    .out(jsonBody[List[Reservation]])
    .tag(tag)

  val findReservationsForClient: Endpoint[Long, String, List[Reservation], Nothing] = baseEndpoint.get
    .in(path[Long]("clientId"))
    .out(jsonBody[List[Reservation]])
    .tag(tag)
}
