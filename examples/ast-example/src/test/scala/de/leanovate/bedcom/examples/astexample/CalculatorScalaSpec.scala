package de.leanovate.bedcom.examples.astexample

import org.scalatest.{ShouldMatchers, FlatSpec}

class CalculatorScalaSpec extends FlatSpec with ShouldMatchers {

  import Calculator._

  it should "used the adapted buildin functions" in {
    calculate("sum2(1,2)") should be(3)
    calculate("sum2(3*3, 10+1)") should be(20)
  }
}
