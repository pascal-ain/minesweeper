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
      ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
        .cross(CrossVersion.for3Use2_13),
      "com.google.inject" % "guice" % "5.1.0",
      ("net.codingwell" %% "scala-guice" % "5.1.0")
        .cross(CrossVersion.for3Use2_13)
    )
  )
scalacOptions ++= Seq("-no-indent", "-rewrite")
