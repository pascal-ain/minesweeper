val scala3Version = "3.2.2-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Minesweeper",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalatest" %% "scalatest" % "3.2.14" % "test",
      "net.team2xh" %% "onions" % "1.0.1"
    )
  )
