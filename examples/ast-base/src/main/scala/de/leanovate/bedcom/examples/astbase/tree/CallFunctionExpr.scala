package de.leanovate.bedcom.examples.astbase.tree

import de.leanovate.bedcom.examples.astbase.context.{UndefinedFunctionException, CalculatorContext}

case class CallFunctionExpr(name: String, parameters: List[Expr]) extends Expr {
  override def eval(implicit context: CalculatorContext) = context.getFunction(name).map {
    function =>
      function.call(parameters.map(_.eval))
  }.getOrElse {
    throw new UndefinedFunctionException(name)
  }
}
