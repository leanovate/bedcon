package de.leanovate.bedcom.examples.combinators

import org.scalatest.{ShouldMatchers, FlatSpec}

class Calculator1InvalidSpec extends FlatSpec with ShouldMatchers {
  val calculator = new Calculator1Invalid

  it should "parse numbers" in {
    calculator.parse("42") should be(42)
    calculator.parse("123") should be(123)
  }

}
