import play.sbt.PlayScala

name := """cart-service"""

//import NativePackagerKeys._
val akkaVersion = "2.4.14"


// setting a maintainer which is used for all packaging types</pre>
maintainer := "Your Name"

enablePlugins(sbtdocker.DockerPlugin)

dockerAutoPackageJavaApplication()

// exposing the play ports
//dockerExposedPorts in Docker := Seq(9000, 9443)

// run this with: docker run -p 9000:9000 <name>:<version>


lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(

  "org.scala-lang" % "scala-compiler" % "2.11.1",
  "com.typesafe.akka" % "akka-cluster-metrics_2.11" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.akka" %% "akka-actor" % "2.4.14",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.11",
  "com.github.krasserm" %% "akka-persistence-cassandra-3x" % "0.6",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.4",
  "org.apache.kafka" %% "kafka" % "0.10.1.0",
  "net.liftweb" %% "lift-json" % "2.6.3",
  "org.apache.ignite" % "ignite-core" % "1.7.0",
  "org.apache.ignite" % "ignite-spring" % "1.7.0",
  "io.netty" % "netty" % "3.3.1.Final"

)
resolvers += "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"


fork in run := false
// https://mvnrepository.com/artifact/org.apache.camel/camel-core
libraryDependencies += "org.apache.camel" % "camel-core" % "2.9.2"


libraryDependencies ++= Seq(

  "commons-lang" % "commons-lang" % "2.6",
  "commons-io" % "commons-io" % "2.4",
  ws
)
libraryDependencies += "commons-net" % "commons-net" % "3.5"
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

// https://mvnrepository.com/artifact/org.apache.ignite/ignite-examples
libraryDependencies += "org.apache.ignite" % "ignite-examples" % "1.0.0-RC1"


libraryDependencies ++= Vector(
  "com.tecsisa" %% "constructr-coordination-consul" % "0.5.2",
  "de.heikoseeberger" %% "constructr-akka" % "0.13.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.3.9" % "test"

)
// https://mvnrepository.com/artifact/org.specs2/specs2_2.9.1
libraryDependencies += "org.specs2" %% "specs2" % "2.3.11" % "test"
//ConstructR+Consul
resolvers += Resolver.bintrayRepo("tecsisa", "maven-bintray-repo")
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

ivyXML := <dependencies>
  <exclude org="org.jboss.netty"/>
</dependencies>
