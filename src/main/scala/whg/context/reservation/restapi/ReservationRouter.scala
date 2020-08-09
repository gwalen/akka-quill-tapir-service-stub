package whg.context.reservation.restapi

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import whg.context.reservation.application.ReservationService
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import sttp.tapir.server.akkahttp._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success


class ReservationRouter(reservationService: ReservationService)(implicit ex: ExecutionContext, mat: Materializer) {
  import ReservationEndpoints._

  private val logger = LoggerFactory.getLogger(getClass)

  val routes: Route =
    createReservation.toRoute((reservationService.createReservation _)) ~
    findReservations.toRoute((reservationService.findAllReservations _).andThen(handleErrors)) ~
    findReservationsForClient.toRoute((reservationService.findReservations _).andThen(handleErrors)) ~
    extendReservation.toRoute((reservationService.extendReservation _).andThen(handleErrors)) ~
    cancelReservation.toRoute((reservationService.cancelReservation _).andThen(handleErrors))

  val docsRoutes: Route = new SwaggerAkka(openapiYamlDocumentation).routes

  val routesWithDocs: Route = routes ~ docsRoutes

  def openapiYamlDocumentation: String = {
    import sttp.tapir.docs.openapi._
    import sttp.tapir.openapi.circe.yaml._

    // interpreting the endpoint description to generate yaml openapi documentation
    val docs = List(
      createReservation,
      findReservationsForClient,
      findReservations,
      extendReservation,
      cancelReservation
    ).toOpenAPI("Ticket reservations", "1.0")
    docs.toYaml
  }

  private def handleErrors[T](f: Future[T]): Future[Either[String, T]] =
    f.transform {
      case Success(v) => Success(Right(v))
      case Failure(e) =>
        logger.error("Exception when running endpoint logic", e)
        Success(Left(e.getMessage))
    }
}