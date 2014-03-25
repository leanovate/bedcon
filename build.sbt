name := "root"

organization := "de.leanovate.bedcon"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

lazy val root = project.in(file(".")).aggregate(astFirst, combinators, astBase, astExample, macroHelloBase, macroHelloUse)

lazy val astFirst = project.in(file("ast-first"))

lazy val astBase = project.in(file("ast-base"))

lazy val astExample = project.in(file("ast-example")).dependsOn(astBase)

lazy val combinators = project

lazy val macroHelloBase = project.in(file("macro-hello-base"))

lazy val macroHelloUse = project.in(file("macro-hello-use")).dependsOn(macroHelloBase)
