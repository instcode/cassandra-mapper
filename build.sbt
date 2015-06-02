name := "cassandra-mapper"

version := "1.0"

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "ibiblio" at "http://mirrors.ibiblio.org/pub/mirrors/maven2",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= {
  Seq(
    "com.typesafe"            % "config"                % "1.2.1",
    "io.spray"               %% "spray-json"            % "1.3.1",
    "com.datastax.cassandra"  % "cassandra-driver-core" % "2.1.6"  exclude("org.xerial.snappy", "snappy-java"),
    "org.xerial.snappy"       % "snappy-java"           % "1.1.1.3",
    "org.scala-lang"          % "scala-reflect"         % "2.11.6",
    "com.typesafe.play"      %% "play-json"             % "2.3.9",
    "com.chuusai"            %% "shapeless"             % "2.1.0",
    "org.specs2"             %% "specs2-mock"           % "3.3.1" % "test",
    "org.specs2"             %% "specs2"                % "3.3.1" % "test"
  )
}

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")