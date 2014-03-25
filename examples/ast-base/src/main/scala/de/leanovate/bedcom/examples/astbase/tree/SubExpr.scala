package de.leanovate.bedcom.examples.astbase.tree

import de.leanovate.bedcom.examples.astbase.context.CalculatorContext

case class SubExpr(left: Expr, right: Expr) extends Expr {
  override def eval(implicit context: CalculatorContext) = left.eval - right.eval
}
