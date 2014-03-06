name := "root"

organization := "de.leanovate.bedcon"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

lazy val root = project.in(file(".")).aggregate(ast, combinators)

lazy val ast = project

lazy val combinators = project
