package de.leanovate.bedcom.examples.astbase.tree

import de.leanovate.bedcom.examples.astbase.context.CalculatorContext

case class NegExpr(expr: Expr) extends Expr {
  override def eval(implicit context: CalculatorContext) = -expr.eval
}
