name := "root"

organization := "de.leanovate.bedcon"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

lazy val root = project.in(file(".")).aggregate(astFirst, combinators, astBase, astExample)

lazy val astFirst = project.in(file("ast-first"))

lazy val astBase = project.in(file("ast-base"))

lazy val astExample = project.in(file("ast-example")).dependsOn(astBase)

lazy val combinators = project
