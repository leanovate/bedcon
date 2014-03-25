package de.leanovate.bedcom.examples.combinators

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical

class Calculator3 extends StdTokenParsers {
  override type Tokens = StdLexical

  override val lexical = new StdLexical

  lexical.delimiters ++= List("(", ")", "+", "-", "*", "/")

  def expr: Parser[Int] = addSub

  def addSub: Parser[Int] = mulDiv * (
    "+" ^^^ {
      (left: Int, right: Int) => left + right
    } | "-" ^^^ {
      (left: Int, right: Int) => left - right
    })

  def mulDiv: Parser[Int] = term * (
    "*" ^^^ {
      (left: Int, right: Int) => left * right
    } | "/" ^^^ {
      (left: Int, right: Int) => left / right
    })

  def term: Parser[Int] = "(" ~> expr <~ ")" | numericLit ^^ (_.toInt)

  def parse(str: String) = expr(new lexical.Scanner(str)) match {
    case Success(result, remain) if remain.atEnd => result
    case Success(_, remain) => throw new RuntimeException(s"Unparsed input at ${remain.pos}")
    case NoSuccess(msg, remain) => throw new RuntimeException(s"Parse error $msg at ${remain.pos}")
  }
}
