package de.leanovate.bedcom.examples.astexample

import org.scalatest.{FlatSpec, ShouldMatchers}

class BuildinFunctionsSpec extends FlatSpec with ShouldMatchers {
  it should "convert all annotated functions" in {
    val names = BuildinFunctions.calculatorFunctions.map(_.name).toSet

    names should contain("sum2")
  }
}
