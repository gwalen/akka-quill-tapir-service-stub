package whg.context.reservation.restapi

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import cats.effect.IO
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
    createReservation.toRoute(toFuture(reservationService.createReservation)) ~
    findReservations.toRoute(toFuture(reservationService.findAllReservations).andThen(handleErrors)) ~
    findReservationsForClient.toRoute(toFuture(reservationService.findReservations _).andThen(handleErrors)) ~
    extendReservation.toRoute(toFuture(reservationService.extendReservation _).andThen(handleErrors)) ~
    cancelReservation.toRoute(toFuture(reservationService.cancelReservation _).andThen(handleErrors))

  val endpoints = List(
    createReservation,
    findReservationsForClient,
    findReservations,
    extendReservation,
    cancelReservation
  )

  private def toFuture[M, K](f: M => IO[K]): M => Future[K] = {
    in => f(in).unsafeToFuture()
  }

  private def handleErrors[T](f: Future[T]): Future[Either[String, T]] =
    f.transform {
      case Success(v) => Success(Right(v))
      case Failure(e) =>
        logger.error("Exception when running endpoint logic", e)
        Success(Left(e.getMessage))
    }
}