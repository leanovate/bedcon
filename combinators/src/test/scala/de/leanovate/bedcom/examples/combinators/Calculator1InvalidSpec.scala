package de.leanovate.bedcom.examples.combinators

import org.scalatest.{ShouldMatchers, FlatSpec}

class Calculator1InvalidSpec extends FlatSpec with ShouldMatchers {
  val calculator = new Calculator1Invalid

  it should "parse numbers" in {
    calculator.parse("42") should be(42)
    calculator.parse("123") should be(123)
  }

  it should "parse simple additions" in {
    calculator.parse("12+23") should be(35)
    calculator.parse("321+123") should be(444)
  }

  it should "parse simple subtractions" in {
    calculator.parse("42-54") should be(-12)
    calculator.parse("23-12") should be(11)
  }

  it should "parse multiple operations (though slightly wrong)" in {
    calculator.parse("42-54+12") should be(-24)
  }
}
