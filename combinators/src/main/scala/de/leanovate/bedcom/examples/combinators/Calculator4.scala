package de.leanovate.bedcom.examples.combinators

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.PackratParsers

class Calculator4 extends StdTokenParsers with PackratParsers {
  override type Tokens = StdLexical

  override val lexical = new StdLexical

  lexical.delimiters ++= List("(", ")", "+", "-", "*", "/")

  lazy val expr: PackratParser[Int] = addSub

  lazy val addSub: PackratParser[Int] = mulDiv * (
    "+" ^^^ {
      (left: Int, right: Int) => left + right
    } | "-" ^^^ {
      (left: Int, right: Int) => left - right
    })

  lazy val mulDiv: PackratParser[Int] = term * (
    "*" ^^^ {
      (left: Int, right: Int) => left * right
    } | "/" ^^^ {
      (left: Int, right: Int) => left / right
    })

  lazy val term: PackratParser[Int] = "(" ~> expr <~ ")" | numericLit ^^ (_.toInt)

  def parse(str: String) = expr(new PackratReader(new lexical.Scanner(str))) match {
    case Success(result, remain) if remain.atEnd => result
    case Success(_, remain) => throw new RuntimeException(s"Unparsed input at ${remain.pos}")
    case NoSuccess(msg, remain) => throw new RuntimeException(s"Parse error $msg at ${remain.pos}")
  }
}
