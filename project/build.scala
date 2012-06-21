import sbt._,Keys._

object build extends Build{

  val buildSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.10.0-M4",
    resolvers ++= Seq(
      "http://xuwei-k.github.com/mvn"
    ).map{u => u at u},
    organization := "com.github.xuwei_k",
    version := "0.1.0-SNAPSHOT",
    shellPrompt in ThisBuild := { state =>
      Project.extract(state).currentRef.project + "> "
    },
    scalacOptions ++= Seq("-deprecation"),
    initialCommands in console := {
      "com.github.xuwei_k".map{"import " + _ + "._"}.mkString("\n")
    },
    libraryDependencies ++= Seq(
      "net.liftweb" % "lift-json_2.9.1" % "2.4-jfield_no_jvalue"
    )
  )

  lazy val root = Project(
    "root",
    file(".")
  )aggregate(json_macro,macrodef)

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

