import sbt._

object Versions {
  val akkaV           = "2.6.6"
  val akkaHttpV       = "10.1.12"
  val postgresqlV     = "42.2.8"
  val quillV          = "3.5.2"
  val slickV          = "3.3.2"
  val slickPgV        = "0.19.0"
  val flywayV         = "6.4.4"
  val macwireV        = "2.3.6"
  val logbackV        = "1.2.3"
  val scalaTestV      = "3.1.2"
  val scalaMockV      = "4.4.0"
  val kebsV           = "1.7.1"
  val catsV           = "2.0.0"
  val commonsIoV      = "2.6"
  val enumeratumV     = "1.5.13"
  val circeV          = "0.13.0"
  val akkaHttpCirceV  = "1.33.0"
  val h2V             = "1.4.196"
  val tapirV          = "0.16.1"
  val logbackEncoderV = "6.0"
}

object Dependencies {
  import Versions._

  lazy val allDependencies: Seq[ModuleID] =
    akkaBase ++ akkaHttp ++ slick ++ macwire ++ logback ++ circe ++ tapir ++ quill ++ others ++ test

  private lazy val akkaBase = Seq(
    "com.typesafe.akka" %% "akka-actor"  % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j"  % akkaV
  )

  private lazy val akkaHttp = Seq(
    "com.typesafe.akka" %% "akka-http-core"       % akkaHttpV,
    "com.typesafe.akka" %% "akka-http"            % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpV % "test",
    "com.typesafe.akka" %% "akka-stream-testkit"  % akkaV % "test"
  )

  private lazy val slick = Seq(
    "org.postgresql"      % "postgresql"           % postgresqlV,
    "com.typesafe.slick"  %% "slick"               % slickV,
    "com.typesafe.slick"  %% "slick-hikaricp"      % slickV,
    "com.github.tminglei" %% "slick-pg"            % slickPgV,
    "com.github.tminglei" %% "slick-pg_spray-json" % slickPgV,
    "com.github.tminglei" %% "slick-pg_jts"        % slickPgV
  )

  private lazy val quill = Seq(
    "org.postgresql" % "postgresql"            % postgresqlV,
    "io.getquill"    %% "quill-async-postgres" % quillV
  )

  private lazy val macwire = Seq(
    "com.softwaremill.macwire" %% "macros" % macwireV % "provided",
    "com.softwaremill.macwire" %% "util"   % macwireV,
    "com.softwaremill.macwire" %% "proxy"  % macwireV
  )

  private lazy val logback = Seq(
    "ch.qos.logback"       % "logback-classic"          % logbackV,
    "net.logstash.logback" % "logstash-logback-encoder" % logbackEncoderV
  )

  private lazy val test = Seq(
    "org.scalatest"  %% "scalatest" % scalaTestV % "test",
    "org.scalamock"  %% "scalamock" % scalaMockV % "test",
    "com.h2database" % "h2"         % h2V        % "test"
  )

  private lazy val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"                 % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"     % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"         % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"   % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"           % tapirV
  )

  private lazy val circe = Seq(
    "io.circe"          %% "circe-core"      % circeV,
    "io.circe"          %% "circe-generic"   % circeV,
    "io.circe"          %% "circe-parser"    % circeV,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceV
  )

  private lazy val others = Seq(
    "com.beachape"      %% "enumeratum"      % enumeratumV,
    "org.typelevel"     %% "cats-core"       % catsV,
    "org.flywaydb"      % "flyway-core"      % flywayV,
    "commons-io"        % "commons-io"       % commonsIoV
  )
}
