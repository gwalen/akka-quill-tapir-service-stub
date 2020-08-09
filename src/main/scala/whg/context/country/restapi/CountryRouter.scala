package whg.context.country.restapi

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
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

  val routes: Route =
    createCountryCurrency.toRoute((countryService.createCountryCurrency _).andThen(handleErrors)) ~
    deleteCountryCurrency.toRoute((countryService.deleteCountryCurrency _).andThen(handleErrors)) ~
    findCountryCurrency.toRoute((countryService.findCountryCurrency _).andThen(handleErrors)) ~
    createCountryTelephonePrefix.toRoute((countryService.createCountryTelephonePrefix _).andThen(handleErrors)) ~
    deleteCountryCurrency.toRoute((countryService.deleteCountryCurrency _).andThen(handleErrors)) ~
    findCountryCurrency.toRoute((countryService.findCountryCurrency _).andThen(handleErrors))

  val docsRoutes: Route = new SwaggerAkka(openapiYamlDocumentation).routes

  val routesWithDocs: Route = routes ~ docsRoutes

  def openapiYamlDocumentation: String = {
    import sttp.tapir.docs.openapi._
    import sttp.tapir.openapi.circe.yaml._

    // interpreting the endpoint description to generate yaml openapi documentation
    val docs = List(
      createCountryCurrency,
      deleteCountryCurrency,
      findCountryCurrency,
      createCountryTelephonePrefix,
      deleteCountryTelephonePrefix,
      findCountryTelephonePrefix
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