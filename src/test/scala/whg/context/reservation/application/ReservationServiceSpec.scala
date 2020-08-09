package whg.context.reservation.application

import java.time.Instant

import akka.Done
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import whg.common.database.PostgresDriver
import whg.context.reservation.domian.Reservation
import whg.context.reservation.domian.ReservationCounter
import whg.context.reservation.domian.dto.ReservationCreateRequest
import whg.context.reservation.domian.dto.ReservationCreateResponses
import whg.context.reservation.domian.dto.ReservationDto
import whg.context.reservation.domian.dto.ReservationExtendRequest
import whg.context.reservation.persistance.ReservationRepository

import scala.concurrent.Future


class ReservationServiceSpec extends AnyFlatSpec with Matchers with MockFactory with ScalaFutures with ScalatestRouteTest { spec =>
  import ReservationServiceSpec._

  it should "return success during reservation when no error" in {
    val postgresDriverStub = stub[PostgresDriver]

    class ReservationRepositoryTest extends ReservationRepository(postgresDriverStub)

    val reservationRepositoryStub = stub[ReservationRepositoryTest]
    val reservationService = new ReservationService(reservationRepositoryStub)

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(Future.successful(Option(reservationCounter)))
    (reservationRepositoryStub.insertWithMaxReservationCheck(_: Reservation)).when(*).returns(Future.successful(1))
    (reservationRepositoryStub.findReservationsForClient(_: Long, _: Long)).when(*, *).returns(Future.successful(clientReservations))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe Right(ReservationCreateResponses.Successful.toString)
  }

  it should "return failure during reservation client has already created a reservation for an event" in {
    val postgresDriverStub = stub[PostgresDriver]

    class ReservationRepositoryTest extends ReservationRepository(postgresDriverStub)

    val reservationRepositoryStub = stub[ReservationRepositoryTest]
    val reservationService = new ReservationService(reservationRepositoryStub)
    val clientReservations = List(Reservation(11, clientId, eventId, 1, Instant.now))

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(Future.successful(Option(reservationCounter)))
    (reservationRepositoryStub.insertWithMaxReservationCheck(_: Reservation)).when(*).returns(Future.successful(1))
    (reservationRepositoryStub.findReservationsForClient(_: Long, _: Long)).when(*, *).returns(Future.successful(clientReservations))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe Left(ReservationCreateResponses.ClientAlreadyHasReservationForEvent.toString)
  }

  it should "return failure during reservation when insert affected rows == 0 (update condition was not met - not enough tickets)" in {
    val postgresDriverStub = stub[PostgresDriver]
    class ReservationRepositoryTest extends ReservationRepository(postgresDriverStub)
    val reservationRepositoryStub = stub[ReservationRepositoryTest]
    val reservationService = new ReservationService(reservationRepositoryStub)

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(Future.successful(Option(reservationCounter)))
    (reservationRepositoryStub.insertWithMaxReservationCheck(_: Reservation)).when(*).returns(Future.successful(0))
    (reservationRepositoryStub.findReservationsForClient(_: Long, _: Long)).when(*, *).returns(Future.successful(clientReservations))

    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe Left(ReservationCreateResponses.NotEnoughTickets.toString)
  }

  it should "return failure during reservation when no reservation counter found" in {
    val postgresDriverStub = stub[PostgresDriver]
    class ReservationRepositoryTest extends ReservationRepository(postgresDriverStub)
    val reservationRepositoryStub = stub[ReservationRepositoryTest]
    val reservationService = new ReservationService(reservationRepositoryStub)
    val eventId = 1000
    val reservationCreateRequest = ReservationCreateRequest(ReservationDto(100, eventId, 1))

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(Future.successful(None))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe Left(ReservationCreateResponses.EventReservationsNotFound.toString)
  }

  it should "return failure during reservation when clients wants to many tickets" in {
    val postgresDriverStub = stub[PostgresDriver]

    class ReservationRepositoryTest extends ReservationRepository(postgresDriverStub)

    val reservationRepositoryStub = stub[ReservationRepositoryTest]
    val reservationService = new ReservationService(reservationRepositoryStub)

    val reservationCreateRequest = ReservationCreateRequest(ReservationDto(100, eventId, 10))

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(Future.successful(Option(reservationCounter)))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe Left(ReservationCreateResponses.TooManyTicketsForClient.toString)
  }

  it should "return correct result when removal was successful" in {
    val postgresDriverStub = stub[PostgresDriver]
    class ReservationRepositoryTest extends ReservationRepository(postgresDriverStub)
    val reservationRepositoryStub = stub[ReservationRepositoryTest]
    val reservationService = new ReservationService(reservationRepositoryStub)

    (reservationRepositoryStub.remove(_: Long)).when(*).returns(Future.successful(1))
    reservationService.cancelReservation(1).futureValue shouldBe Done
  }

  it should "return correct result when update of expiry was successful" in {
    val postgresDriverStub = stub[PostgresDriver]
    class ReservationRepositoryTest extends ReservationRepository(postgresDriverStub)
    val reservationRepositoryStub = stub[ReservationRepositoryTest]
    val reservationService = new ReservationService(reservationRepositoryStub)

    val reservationExtendRequest = ReservationExtendRequest(1, Instant.now)

    (reservationRepositoryStub.updateReservationExpiryDate(_: Long, _: Instant)).when(*, *).returns(Future.successful(1))
    reservationService.extendReservation(reservationExtendRequest).futureValue shouldBe Done
  }

  it should "return correct result when find query was successful" in {
    val postgresDriverStub = stub[PostgresDriver]
    class ReservationRepositoryTest extends ReservationRepository(postgresDriverStub)
    val reservationRepositoryStub = stub[ReservationRepositoryTest]
    val reservationService = new ReservationService(reservationRepositoryStub)

    val reservationsFromDb = List(Reservation(1, 1, 1, 1, Instant.MAX))

    (reservationRepositoryStub.findAllReservations _).when().returns(Future.successful(reservationsFromDb))
    reservationService.findAllReservations().futureValue shouldBe reservationsFromDb
  }

}

object ReservationServiceSpec {
  val eventId = 1000
  val clientId = 100
  val reservationCreateRequest = ReservationCreateRequest(ReservationDto(clientId, eventId, 1))
  val reservationCounter = ReservationCounter(eventId, 500, 0, 5)
  val clientReservations = List()
}