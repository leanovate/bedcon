---
layout: page
title: "Abstract syntax tree"
description: ""
breadcrumb: [index.md]
---

![Abstract syntax tree](abstract_syntax_tree.png)

{% highlight scala %}
trait Node { }

trait Expr extends Node {
  def eval(implicit context: CalculatorContext): Int
}

case class AddExpr(left: Expr, right: Expr) extends Expr {
  override def eval(implicit context: CalculatorContext) = left.eval + right.eval
}

case class SubExpr(left: Expr, right: Expr) extends Expr {
  override def eval(implicit context: CalculatorContext) = left.eval - right.eval
}

case class VarGetExpr(name: String) extends Expr {
  override def eval(implicit context: CalculatorContext) = context.getVariable(name).getOrElse {
    throw new RuntimeException(s"Variable $name not defined")
  }
}

case class AssignExpr(name: String, expr: Expr) extends Expr {
  override def eval(implicit context: CalculatorContext) = {

    val value = expr.eval
    context.setVariable(name, value)
    value
  }
}
...
{% endhighlight %}

{% highlight scala %}
object ASTParser extends StdTokenParsers {
  override type Tokens = StdLexical

  override val lexical = new StdLexical

  lexical.delimiters ++= List("(", ")", "+", "-", "*", "/", "=", ";")

  def exprs: Parser[Expr] = repsep(expr, ";") ^^ ExprListExpr

  def expr: Parser[Expr] = assign | addSub

  def assign: Parser[Expr] = ident ~ "=" ~ addSub ^^ { case name ~ _ ~ valueExpr => AssignExpr(name, valueExpr) }

  def addSub: Parser[Expr] = mulDiv * ("+" ^^^ AddExpr | "-" ^^^ SubExpr)

  def mulDiv: Parser[Expr] = term * ("*" ^^^ MulExpr | "/" ^^^ DivExpr)

  def term: Parser[Expr] = 
    "(" ~> expr <~ ")" | "-" ~> expr ^^ NegExpr | ident ^^ VarGetExpr | numericLit ^^ (str => LiteralExpr(str.toInt))
...
}
{% endhighlight %}

