
name         := "lookup-service"
organization := "whg"
version      := "1.0.0"
scalaVersion := "2.13.2"
scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  //"-Xfatal-warnings",
  //"-Ywarn-value-discard",
  "-Xlint:missing-interpolator"
)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Dependencies.allDependencies

// Migrations
enablePlugins(FlywayPlugin)
PostgresMigrations.settings

// SBT Native packager
enablePlugins(JavaAppPackaging, DockerPlugin, DatadogAPM)

// DataDog agent configuration
val dd_host = sys.env.getOrElse("DD_HOST_IP", "none")
datadogEnableAkkaHttp := true
datadogAgentPort := "8126"
datadogAgentHost := dd_host

//docker file settings
dockerExposedPorts ++= Seq(8080, 8126)
dockerBaseImage := "adoptopenjdk/openjdk11:slim"
dockerUpdateLatest := true

// to start app in separate JVM and allow killing it without restarting SBT (developing purpose)
run / fork := true