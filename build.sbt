import sbt.Keys.scalaVersion
import sbt._

name := "Inference"
scalaVersion := Libraries.scalaMainVersion

// ************************************************
// ************************************************
// Common Settings
// ************************************************
// ************************************************
lazy val commonSettings = {
  import Libraries._
  Seq(
    organizationName := "RAP",
    version := "0.0.1",
    scalaVersion := Libraries.scalaMainVersion,
    scalacOptions ++= CompilerSettings.compilerFlags,
    discoveredMainClasses := Seq.empty,
    libraryDependencies ++= cats ++ fs2 ++ ciris ++ circe ++ http4s ++ log4cats,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(
      ("org.typelevel" %% "kind-projector" % "0.13.2").cross(CrossVersion.full)
    )
  )
}

// ************************************************
// ************************************************
// Projects
// ************************************************
// ************************************************
lazy val root = project
  .in(file("."))
  .settings(
    name := "inference",
    scalaVersion := Libraries.scalaMainVersion
  )
  .aggregate(
    inferenceApp,
    inferenceRoutes,
    inferenceAlgebra,
    model,
  )

// ************************************************
// Faction
// ************************************************
lazy val inferenceApp = project
  .in(file("./modules/app"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    name := "app",
    mainClass := Option("upt.se.infer.app.Inference"),
    dockerExposedPorts := Seq(31513),
    dockerBaseImage := "openjdk:16-jdk",
    commonSettings
  )
  .dependsOn(model, inferenceRoutes)

lazy val inferenceRoutes = project
  .in(file("./modules/routes"))
  .settings(
    name := "routes",
    commonSettings
  )
  .dependsOn(model, inferenceAlgebra)

lazy val inferenceAlgebra = project
  .in(file("./modules/algebra"))
  .settings(
    name := "algebra",
    commonSettings
  )
  .dependsOn(model)


lazy val model = project
  .in(file("./modules/model"))
  .settings(
    name := "model",
    commonSettings
  )