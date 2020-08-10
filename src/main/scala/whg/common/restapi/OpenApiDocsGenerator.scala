package whg.common.restapi

import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import sttp.tapir.Endpoint
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.concurrent.ExecutionContext

class OpenApiDocsGenerator()(implicit ex: ExecutionContext, mat: Materializer) {

  def openapiYamlDocumentation(endpoints: Iterable[Endpoint[_, _, _, _]]): String = {
    import sttp.tapir.docs.openapi._
    import sttp.tapir.openapi.circe.yaml._

    // interpreting the endpoint description to generate yaml openapi documentation
    val docs = endpoints.toOpenAPI("Lookup service endpoints", "1.0")
    docs.toYaml
  }

  def docRoutes(endpoints: Iterable[Endpoint[_, _, _, _]]): Route = {
    val openApiYaml = openapiYamlDocumentation(endpoints)
    new SwaggerAkka(openApiYaml).routes
  }

}
