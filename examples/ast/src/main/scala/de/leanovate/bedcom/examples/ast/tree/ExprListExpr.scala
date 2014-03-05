package de.leanovate.bedcom.examples.ast.tree

case class ExprListExpr(exprs: List[Expr]) extends Expr {
  override def eval(implicit context: CalculatorContext) = exprs.foldLeft(0) {
    (_, expr) => expr.eval
  }
}
