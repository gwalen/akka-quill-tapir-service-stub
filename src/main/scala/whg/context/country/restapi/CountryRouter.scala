package whg.context.country.restapi

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import cats.effect.IO
import org.slf4j.LoggerFactory
import sttp.tapir.server.akkahttp._
import sttp.tapir.swagger.akkahttp.SwaggerAkka
import whg.context.country.application.CountryService

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success


class CountryRouter(countryService: CountryService)(implicit ex: ExecutionContext, mat: Materializer) {

  import CountryEndpoints._

  private val logger = LoggerFactory.getLogger(getClass)

  val routes: Route = {
    createCountryCurrency.toRoute(toFuture(countryService.createCountryCurrency).andThen(handleErrors)) ~
    deleteCountryCurrency.toRoute(toFuture(countryService.deleteCountryCurrency).andThen(handleErrors)) ~
    findCountryCurrency.toRoute(toFuture(countryService.findCountryCurrency).andThen(handleErrors)) ~
    findAllCountryCurrencies.toRoute(toFuture(countryService.findAllCountryCurrencies).andThen(handleErrors)) ~
    createCountryTelephonePrefix.toRoute(toFuture(countryService.createCountryTelephonePrefix).andThen(handleErrors)) ~
    deleteCountryTelephonePrefix.toRoute(toFuture(countryService.deleteCountryTelephonePrefix).andThen(handleErrors)) ~
    findCountryTelephonePrefix.toRoute(toFuture(countryService.findCountryTelephonePrefix).andThen(handleErrors)) ~
    findAllCountryTelephonePrefixes.toRoute(toFuture(countryService.findAllTelephonePrefixes).andThen(handleErrors))
  }

  val endpoints = List(
    createCountryCurrency,
    deleteCountryCurrency,
    findCountryCurrency,
    findAllCountryCurrencies,
    createCountryTelephonePrefix,
    deleteCountryTelephonePrefix,
    findCountryTelephonePrefix,
    findAllCountryTelephonePrefixes
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