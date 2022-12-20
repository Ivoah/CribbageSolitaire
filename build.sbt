enablePlugins(ScalaJSPlugin)

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "CribbageSolitaire",
    idePackagePrefix := Some("net.ivoah.cribbagesolitaire"),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.1.0",
    ),
    scalaJSUseMainModuleInitializer := true
  )
