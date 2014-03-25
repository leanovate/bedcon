package de.leanovate.bedcom.examples.combinators

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharSequenceReader

class Calculator2Whitespace extends Parsers {
  type Elem = Char

  def expr = addSub

  def addSub: Parser[Int] = mulDiv * (
    '+' ^^^ {
      (left: Int, right: Int) => left + right
    } | '-' ^^^ {
      (left: Int, right: Int) => left - right
    })

  def mulDiv = number * (
    '*' ^^^ {
      (left: Int, right: Int) => left * right
    } | '/' ^^^ {
      (left: Int, right: Int) => left / right
    })

  def number = whitespace.* ~> digit.+ <~ whitespace.* ^^ {
    digits => digits.mkString("").toInt
  }

  def digit = elem("digit", _.isDigit)

  def whitespace = elem("whitespace", ch => ch == ' ' || ch == '\t')

  def parse(str: String) = expr(new CharSequenceReader(str)) match {
    case Success(result, remain) if remain.atEnd => result
    case Success(_, remain) => throw new RuntimeException(s"Unparsed input at ${remain.pos}")
    case NoSuccess(msg, remain) => throw new RuntimeException(s"Parse error $msg at ${remain.pos}")
  }

}
