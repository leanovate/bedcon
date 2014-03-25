package de.leanovate.bedcom.examples.astbase.tree

import de.leanovate.bedcom.examples.astbase.context.CalculatorContext

case class ExprListExpr(exprs: List[Expr]) extends Expr {
  override def eval(implicit context: CalculatorContext) = exprs.foldLeft(0) {
    (_, expr) => expr.eval
  }
}
