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

  val baseEndpoint: Endpoint[Unit, String, Unit, Nothing] = endpoint.errorOut(stringBody).in("countries")

  val createCountryCurrency: Endpoint[CountryCurrencyCreateRequest, String, Done, Nothing] = baseEndpoint.post
    .in(jsonBody[CountryCurrencyCreateRequest])
    .out(jsonBody[Done])

  val deleteCountryCurrency: Endpoint[String, String, Done, Nothing] = baseEndpoint.delete
    .in(path[String])
    .out(jsonBody[Done])

  val findCountryCurrency: Endpoint[String, String, Option[CountryCurrency], Nothing] = baseEndpoint.get
    .in(path[String]("currency"))
    .out(jsonBody[Option[CountryCurrency]])

  val createCountryTelephonePrefix: Endpoint[CountryTelephonePrefixCreateRequest, String, Done, Nothing] = baseEndpoint.post
    .in(jsonBody[CountryTelephonePrefixCreateRequest])
    .out(jsonBody[Done])

  val deleteCountryTelephonePrefix: Endpoint[String, String, Done, Nothing] = baseEndpoint.delete
    .in(path[String])
    .out(jsonBody[Done])

  val findCountryTelephonePrefix: Endpoint[String, String, Option[CountryTelephonePrefix], Nothing] = baseEndpoint.get
    .in(path[String]("currency"))
    .out(jsonBody[Option[CountryTelephonePrefix]])
}
