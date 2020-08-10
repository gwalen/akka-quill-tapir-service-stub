package whg.context.country.restapi

import akka.Done
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.json.circe._
import whg.context.country.domain.CountryCurrency
import whg.context.country.domain.CountryTelephonePrefix
import whg.context.country.domain.dto.CountryCurrencyCreateRequest
import whg.context.country.domain.dto.CountryTelephonePrefixCreateRequest

object CountryEndpoints {

  private val tag = "country"

  val baseEndpoint: Endpoint[Unit, String, Unit, Nothing] = endpoint.errorOut(stringBody).in("api").in("countries")

  val createCountryCurrency: Endpoint[CountryCurrencyCreateRequest, String, Done, Nothing] = baseEndpoint.post
    .in("currencies")
    .in(jsonBody[CountryCurrencyCreateRequest])
    .out(jsonBody[Done])
    .tag(tag)
    .name("create xxx")

  val deleteCountryCurrency: Endpoint[String, String, Done, Nothing] = baseEndpoint.delete
    .in("currencies")
    .in(path[String]("country"))
    .out(jsonBody[Done])
    .tag(tag)


  val findCountryCurrency: Endpoint[String, String, Option[CountryCurrency], Nothing] = baseEndpoint.get
    .in("currencies")
    .in(path[String]("country"))
    .out(jsonBody[Option[CountryCurrency]])
    .tag(tag)

  val findAllCountryCurrencies: Endpoint[Unit, String, List[CountryCurrency], Nothing] = baseEndpoint.get
    .in("currencies")
    .out(jsonBody[List[CountryCurrency]])
    .tag(tag)

  val createCountryTelephonePrefix: Endpoint[CountryTelephonePrefixCreateRequest, String, Done, Nothing] = baseEndpoint.post
    .in("telephoneprefixes")
    .in(jsonBody[CountryTelephonePrefixCreateRequest])
    .out(jsonBody[Done])
    .tag(tag)

  val deleteCountryTelephonePrefix: Endpoint[String, String, Done, Nothing] = baseEndpoint.delete
    .in("telephoneprefixes")
    .in(path[String]("country"))
    .out(jsonBody[Done])
    .tag(tag)

  val findCountryTelephonePrefix: Endpoint[String, String, Option[CountryTelephonePrefix], Nothing] = baseEndpoint.get
    .in("telephoneprefixes")
    .in(path[String]("country"))
    .out(jsonBody[Option[CountryTelephonePrefix]])
    .tag(tag)

  val findAllCountryTelephonePrefixes: Endpoint[Unit, String, List[CountryTelephonePrefix], Nothing] = baseEndpoint.get
    .in("telephoneprefixes")
    .out(jsonBody[List[CountryTelephonePrefix]])
    .tag(tag)
}
