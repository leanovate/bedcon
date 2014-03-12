---
layout: page
title: "Scala marcos: Calculator example"
description: ""
breadcrumb: [index.md, talk/runtime_library.md, talk/generate_glue_code.md]
---

{% highlight scala linenos %}
trait CalculatorFunction {
  def name: String

  def call(parameters: Seq[Int]): Int
}

class CalculatorContext {
...
  private val functions = mutable.Map.empty[String, CalculatorFunction]

  def getFunction(name: String): Option[CalculatorFunction] = functions.get(name)

  def registerFunction(calculatorFunction: CalculatorFunction) = functions.put(calculatorFunction.name, calculatorFunction)
}
{% endhighlight %}

{% highlight scala linenos %}
case class CallFunctionExpr(name: String, parameters: List[Expr]) extends Expr {
  override def eval(implicit context: CalculatorContext) = context.getFunction(name).map {
    function =>
      function.call(parameters.map(_.eval))
  }.getOrElse {
    throw new RuntimeException(s"Function $name not defined")
  }
}
{% endhighlight %}

{% highlight scala linenos %}
object ASTParser extends StdTokenParsers {
...
  def term: Parser[Expr] = "(" ~> expr <~ ")" | "-" ~> expr ^^ NegExpr |
    ident ~ "(" ~ repsep(expr, ",") ~ ")" ^^ {
      case name ~ _ ~ parameters ~ _ => CallFunctionExpr(name, parameters)
    } | 
    ident ^^ VarGetExpr | 
    numericLit ^^ (str => LiteralExpr(str.toInt))
...
{% endhighlight %}

{% highlight scala linenos %}
object FunctionAdapter {
  def generateCalculatorFunctions(inst: Any): Seq[CalculatorFunction] = macro generateCalculatorFunctions_impl

  def generateCalculatorFunctions_impl(c: Context)(inst: c.Expr[Any]): c.Expr[Seq[CalculatorFunction]] = {
    import c.universe._

    reify {
      Seq()
    }
  }
}
{% endhighlight %}

