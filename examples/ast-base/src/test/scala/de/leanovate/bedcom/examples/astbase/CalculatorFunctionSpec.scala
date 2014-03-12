package de.leanovate.bedcom.examples.astbase

import org.scalatest._
import de.leanovate.bedcom.examples.astbase.context.{UndefinedVariableException, UndefinedFunctionException}

class CalculatorFunctionSpec extends FlatSpec with ShouldMatchers {

  import TestCalculator.calculate

  it should "support a simple function calls" in {
    calculate("one()") should be(1)
    calculate("sum(1,2,3)") should be(6)
    calculate("sum(1, 2, 1+2, 2*2)") should be(10)
  }

  it should "throw an exception if function does not exists" in {

    intercept[UndefinedFunctionException] {
      calculate("2 + unknown(1)")
    }
  }

  it should "throw an exception if variable does not exists" in {

    intercept[UndefinedVariableException] {
      calculate("1 + unknown")
    }
  }
}
