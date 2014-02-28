package de.leanovate.bedcom.examples.combinators

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharSequenceReader

class Calculator1Invalid extends Parsers {
  type Elem = Char

  def expr: Parser[Int] = addition | subtraction | number

  def addition: Parser[Int] = number ~ '+' ~ expr ^^ {
    case left ~ _ ~ right => left + right
  }

  def subtraction: Parser[Int] = number ~ '-' ~ expr ^^ {
    case left ~ _ ~ right => left - right
  }

  def number: Parser[Int] = digit.+ ^^ {
    digits => digits.mkString("").toInt
  }

  def digit: Parser[Char] = elem("digit", _.isDigit)

  def parse(str: String) = expr(new CharSequenceReader(str)) match {
    case Success(result, remain) if remain.atEnd => result
    case Success(_, remain) => throw new RuntimeException(s"Unparsed input at ${remain.pos}")
    case NoSuccess(msg, remain) => throw new RuntimeException(s"Parse error $msg at ${remain.pos}")
  }
}

object Calculator1Invalid extends App {
  val calculator = new Calculator1Invalid

  println(calculator.parse("42"))
  println(calculator.parse("42+54"))
  println(calculator.parse("42-54"))
  println(calculator.parse("42-54+12"))
}