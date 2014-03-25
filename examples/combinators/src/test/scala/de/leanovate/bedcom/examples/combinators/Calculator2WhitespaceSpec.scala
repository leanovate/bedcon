package de.leanovate.bedcom.examples.combinators

import org.scalatest._

class Calculator2WhitespaceSpec extends FlatSpec with ShouldMatchers {
  val calculator = new Calculator2Whitespace

  it should "parse numbers" in {
    calculator.parse("42") should be(42)
    calculator.parse("123") should be(123)
    calculator.parse(" \t42  ") should be(42)
    calculator.parse(" \t123  ") should be(123)
  }

  it should "parse simple additions" in {
    calculator.parse("12+23") should be(35)
    calculator.parse("321+123") should be(444)
    calculator.parse(" 12 +\t23 ") should be(35)
    calculator.parse(" 321 +\t123 ") should be(444)
  }

  it should "parse simple subtractions" in {
    calculator.parse("42-54") should be(-12)
    calculator.parse("23-12") should be(11)
    calculator.parse(" 42 - 54") should be(-12)
    calculator.parse(" 23 - 12") should be(11)
  }

  it should "parse simple multiplication" in {
    calculator.parse("12*23") should be(276)
    calculator.parse("3*4") should be(12)
    calculator.parse("12 * 23") should be(276)
    calculator.parse(" 3 *4") should be(12)
  }

  it should "parse simple division" in {
    calculator.parse("12/4") should be(3)
    calculator.parse("276/12") should be(23)
    calculator.parse("12\t/4") should be(3)
    calculator.parse("276 /\t12") should be(23)
  }

  it should "parse multiple operations" in {
    calculator.parse("42-3*3*3*2+24/2") should be(0)
    calculator.parse("42 - 3*3*3*2 + 24/2") should be(0)
  }
}
