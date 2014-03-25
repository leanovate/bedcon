name := "ast-base"

organization := "de.leanovate.bedcon"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
                             "org.scala-lang" % "scala-reflect" % scalaVersion.value,
                             "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
                           )
