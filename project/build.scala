import sbt._,Keys._

object build extends Build{

  val buildSettings = Defaults.defaultSettings ++ seq(
    scalaVersion := "2.10.3",
    resolvers ++= Seq(Opts.resolver.sonatypeReleases),
    organization := "com.github.xuwei-k",
    version := "0.1.0-SNAPSHOT",
    scalacOptions ++= Seq("-deprecation"),
    initialCommands in console := {
      "com.github.xuwei_k".map{"import " + _ + "._"}.mkString("\n")
    },
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % "3.2.5"
    )
  )

  lazy val root = Project(
    "root",
    file("."),
    settings = buildSettings
  ).aggregate(json_macro,macrodef)

  lazy val json_macro = Project(
    "json_macro",
    file("json_macro"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(
      )
    )
  )dependsOn(macrodef)

  lazy val macrodef = Project(
    "macrodef",
    file("macrodef"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion{ v =>
        Seq(
          "org.scala-lang" % "scala-reflect" % v
        )
      }
    )
  )
}

