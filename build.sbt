lazy val CribbageSolitaire = crossProject(JVMPlatform, JSPlatform).crossType(CrossType.Full).in(file("."))
  .settings(
    name := "Cribbage Solitaire",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.3.5",
    scalacOptions ++= Seq("-deprecation", "-feature"),
    libraryDependencies ++= Seq(
      // "org.playframework" %%% "play-json" % "3.0.4",
      // "com.lihaoyi" %%% "pprint" % "0.9.0",
      // "net.ivoah" %% "slippy" % "0.1.0"
    ),
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      // "org.rogach" %% "scallop" % "5.2.0"
    ),
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.5.0",
      // "com.lihaoyi" %%% "scalatags" % "0.12.0"
    ),
    scalaJSUseMainModuleInitializer := true
  )
