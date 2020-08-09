logLevel := Level.Warn

resolvers += "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"

addSbtPlugin("io.github.davidmweber" % "flyway-sbt"          % "6.4.2")
addSbtPlugin("com.typesafe.sbt"      % "sbt-native-packager" % "1.7.3")
addSbtPlugin("au.com.onegeek"        %% "sbt-dotenv"         % "2.1.146")
