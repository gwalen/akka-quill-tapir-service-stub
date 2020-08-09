package whg.context.reservation.application

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.Materializer
import cats.data.EitherT
import cats.implicits._
import whg.context.reservation.domian._
import whg.context.reservation.domian.dto.ReservationCreateRequest
import whg.context.reservation.domian.dto.ReservationCreateResponse
import whg.context.reservation.domian.dto.ReservationCreateResponses
import whg.context.reservation.domian.dto.ReservationExtendRequest
import whg.context.reservation.persistance.ReservationRepository

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Either

class ReservationService(reservationRepository: ReservationRepository)(implicit ec: ExecutionContext, mat: Materializer, system: ActorSystem) {

  private val logger = Logging(system, getClass)

  def createReservation(request: ReservationCreateRequest): Future[Either[String, String]] = {
    logger.info(s"Create reservation for: $request")
    val reservation = Reservation.from(request.reservationDto)
    val createResult = for {
      reservationCounter <- findReservationCounter(reservation.eventId)
      _                  <- checkMaxNumberOfTicketsForClient(reservation, reservationCounter)
      result             <- addReservation(reservation)
    } yield result

    createResult.value.map {
      case Right(r) => Right(r.toString)
      case Left(l)  => Left(l.toString)
    }
  }

  def extendReservation(request: ReservationExtendRequest): Future[Done] = {
    logger.info(s"Extend reservation for: $request")
    reservationRepository.updateReservationExpiryDate(request.reservationId, request.newExpiryDate).map(_ => Done)
  }

  def cancelReservation(reservationId: Long): Future[Done] = {
    logger.info(s"Cancel reservation : $reservationId")
    reservationRepository.remove(reservationId).map(_ => Done)
  }

  //unit type arg added to be able to chain Functions (Function1 with andThen() method)
  def findAllReservations(x: Unit): Future[List[Reservation]] = {
    logger.info(s"Get all reservations")
    reservationRepository.findAllReservations().map(_.toList)
  }

  def findReservations(eventId: Long): Future[List[Reservation]] = {
    logger.info(s"Get all reservations for event = $eventId")
    reservationRepository.findAllReservationsForEvent(eventId).map(_.toList)
  }

  private def checkMaxNumberOfTicketsForClient(
    reservation: Reservation,
    reservationCounter: ReservationCounter
  ): EitherT[Future, ReservationCreateResponse, Unit] = {
    for {
      _ <- checkIfClientReservesTooManyTickets(reservation, reservationCounter)
      _ <- checkIfClientHasReservationForEvent(reservation.eventId, reservation.clientId)
    } yield ()
  }

  private def findReservationCounter(eventId: Long): EitherT[Future, ReservationCreateResponse, ReservationCounter] = {
    val reservationCounter = reservationRepository.findReservationCounter(eventId)
    EitherT.fromOptionF(reservationCounter, ReservationCreateResponses.EventReservationsNotFound)
  }

  private def addReservation(reservation: Reservation): EitherT[Future, ReservationCreateResponse, ReservationCreateResponse] = {
    val insertResult : Future[Either[ReservationCreateResponse, ReservationCreateResponse]] =
      reservationRepository.insertWithMaxReservationCheck(reservation)
        .map {
          case rowsAffected if rowsAffected == 0 => Either.left(ReservationCreateResponses.NotEnoughTickets)
          case rowsAffected if rowsAffected > 0 => Either.right(ReservationCreateResponses.Successful)
        }
    EitherT(insertResult)
  }

  private def checkIfClientHasReservationForEvent(eventId: Long, clientId: Long): EitherT[Future, ReservationCreateResponse, Unit] = {
    val clientReservationsForEvent: Future[Either[ReservationCreateResponse, Unit]] = reservationRepository
      .findReservationsForClient(eventId, clientId)
        .map(r => if(r.nonEmpty) Left(ReservationCreateResponses.ClientAlreadyHasReservationForEvent) else Right(()))
    EitherT(clientReservationsForEvent)
  }

  private def checkIfClientReservesTooManyTickets(
    reservation: Reservation,
    reservationCounter: ReservationCounter
  ): EitherT[Future, ReservationCreateResponse, Unit] = {
    if (reservation.ticketCount > reservationCounter.maxTicketsPerClient) {
      val tooManyTicketsForClient: Future[Either[ReservationCreateResponse, Unit]] = Future.successful(Either.left(ReservationCreateResponses.TooManyTicketsForClient))
      EitherT(tooManyTicketsForClient)
    } else {
      EitherT(Future.successful(Either.right(())))
    }
  }

}
