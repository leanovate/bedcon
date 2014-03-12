package de.leanovate.bedcom.examples.astbase.parser

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import de.leanovate.bedcom.examples.astbase.tree._
import de.leanovate.bedcom.examples.astbase.tree.AddExpr
import de.leanovate.bedcom.examples.astbase.tree.ExprListExpr
import de.leanovate.bedcom.examples.astbase.tree.AssignExpr

object ASTParser extends StdTokenParsers {
  override type Tokens = StdLexical

  override val lexical = new StdLexical

  lexical.delimiters ++= List("(", ")", "+", "-", "*", "/", "=", ";", ",")

  def exprs: Parser[Expr] = repsep(expr, ";") ^^ ExprListExpr

  def expr: Parser[Expr] = assign | addSub

  def assign: Parser[Expr] = ident ~ "=" ~ addSub ^^ {
    case name ~ _ ~ valueExpr => AssignExpr(name, valueExpr)
  }

  def addSub: Parser[Expr] = mulDiv * ("+" ^^^ AddExpr | "-" ^^^ SubExpr)

  def mulDiv: Parser[Expr] = term * ("*" ^^^ MulExpr | "/" ^^^ DivExpr)

  def term: Parser[Expr] = "(" ~> expr <~ ")" | "-" ~> expr ^^ NegExpr |
    ident ~ "(" ~ repsep(expr, ",") ~ ")" ^^ {
      case name ~ _ ~ parameters ~ _ => CallFunctionExpr(name, parameters)
    } | ident ^^ VarGetExpr | numericLit ^^ (str => LiteralExpr(str.toInt))

  def parse(str: String) = exprs(new lexical.Scanner(str)) match {
    case Success(result, remain) if remain.atEnd => result
    case Success(_, remain) => throw new RuntimeException(s"Unparsed input at ${remain.pos}")
    case NoSuccess(msg, remain) => throw new RuntimeException(s"Parse error $msg at ${remain.pos}")
  }
}
