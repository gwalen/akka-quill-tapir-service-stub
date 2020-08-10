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

  //unit type arg added to be able to chain Functions in CountryRouter (Function1 with andThen() method)
  def findAllCountryCurrencies(x: Unit): Future[List[CountryCurrency]] = {
    logger.info(s"Find All country currencies")
    countryRepository.findAllCurrencies()
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

  //unit type arg added to be able to chain Functions in CountryRouter (Function1 with andThen() method)
  def findAllTelephonePrefixes(x: Unit): Future[List[CountryTelephonePrefix]] = {
    logger.info(s"Find all country tel prefixes")
    countryRepository.findAllTelephonePrefixes()
  }

}
