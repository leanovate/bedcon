package de.leanovate.bedcom.examples.ast.tree

case class MulExpr(left: Expr, right: Expr) extends Expr {
  override def eval(implicit context: CalculatorContext) = left.eval * right.eval
}
