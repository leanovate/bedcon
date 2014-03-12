package de.leanovate.bedcom.examples.astbase

import org.scalatest._

class CalculatorSpec extends FlatSpec with ShouldMatchers {

  import TestCalculator.calculate

  it should "parse numbers" in {
    calculate("42") should be(42)
    calculate("123") should be(123)
    calculate(" \t42  ") should be(42)
    calculate(" \t123  ") should be(123)
  }

  it should "parse simple additions" in {
    calculate("12+23") should be(35)
    calculate("321+123") should be(444)
    calculate(" 12 +\t23 ") should be(35)
    calculate(" 321 +\t123 ") should be(444)
  }

  it should "parse simple subtractions" in {
    calculate("42-54") should be(-12)
    calculate("23-12") should be(11)
    calculate(" 42 - 54") should be(-12)
    calculate(" 23 - 12") should be(11)
  }

  it should "parse simple multiplication" in {
    calculate("12*23") should be(276)
    calculate("3*4") should be(12)
    calculate("12 * 23") should be(276)
    calculate(" 3 *4") should be(12)
  }

  it should "parse simple division" in {
    calculate("12/4") should be(3)
    calculate("276/12") should be(23)
    calculate("12\t/4") should be(3)
    calculate("276 /\t12") should be(23)
  }

  it should "parse multiple operations" in {
    calculate("42-3*3*3*2+24/2") should be(0)
    calculate("42 - 3*3*3*2 + 24/2") should be(0)
    calculate("42-3*3*3*2+24/2") should be(0)
    calculate("2 * (3+4) / 2 + 7 * 5") should be(42)
    calculate("2 * (3 /* blah blah */ +4) / 2 + 7 * 5") should be(42)
  }

  it should "handle uniare - (negation)" in {
    calculate("-42") should be(-42)
    calculate("42+ -54") should be(-12)
    calculate("-3 * -4") should be(12)
  }

  it should "be able to assign an use variables" in {
    calculate("a=1; b=2; a+b") should be(3)
    calculate("a=3; b=2 * a; a+b") should be(9)
    calculate("a=276; b=a/12; c=2*b + a; c") should be(322)
  }
}
