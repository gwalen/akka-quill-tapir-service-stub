package whg.context.country.application

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.Materializer
import whg.context.country.domain.CountryCurrency
import whg.context.country.domain.CountryTelephonePrefix
import whg.context.country.domain.dto.CountryCurrencyCreateRequest
import whg.context.country.domain.dto.CountryTelephonePrefixCreateRequest
import whg.context.country.persistance.CountryRepository

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class CountryService(countryRepository: CountryRepository)(implicit ec: ExecutionContext, mat: Materializer, system: ActorSystem) {

  private val logger = Logging(system, getClass)

  def createCountryCurrency(request: CountryCurrencyCreateRequest): Future[Done] = {
    logger.info(s"Insert country currency: ${request.countryCurrency}")
    countryRepository.insertCurrency(request.countryCurrency)
  }

  def deleteCountryCurrency(country: String): Future[Done] = {
    logger.info(s"Delete country currency: $country")
    countryRepository.deleteCurrency(country)
  }

  def findCountryCurrency(country: String): Future[Option[CountryCurrency]] = {
    logger.info(s"Find country currency: $country")
    countryRepository.findCurrency(country)
  }

  def createCountryTelephonePrefix(request: CountryTelephonePrefixCreateRequest): Future[Done] = {
    logger.info(s"Create country tel prefix: ${request.countryTelephonePrefix}")
    countryRepository.insertTelephonePrefix(request.countryTelephonePrefix)
  }

  def deleteCountryTelephonePrefix(country: String): Future[Done] = {
    logger.info(s"Delete country tel prefix: $country")
    countryRepository.deleteTelephonePrefix(country)
  }

  def findCountryTelephonePrefix(country: String): Future[Option[CountryTelephonePrefix]] = {
    logger.info(s"Find country tel prefix: $country")
    countryRepository.findTelephonePrefix(country)
  }

// TODO:
// - add find methods for all currencies and all prefixes
// - test json logging
// test reservations insert/update logic
// test swagger open api
// create read me with list of technologies and swagger open api usage

}
