val scala3Version = "3.3.1"


lazy val caos = project.in(file("lib/caos"))
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaVersion := scala3Version)

lazy val clientPr = project.in(file("."))
   .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "client PR",
    version := "0.1.0",
    scalaVersion := scala3Version,
    scalaJSUseMainModuleInitializer := true,
    Compile / mainClass := Some("clientPr.frontend.Main"),
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "lib" / "caos"/ "tool" / "js" / "gen",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-parse" % "0.3.4",  // parser combinators
      "org.scalameta" %% "munit" % "0.7.29" % Test // unit tests
    )
  )
  .dependsOn(caos)

lazy val server = project.in(file("Server"))