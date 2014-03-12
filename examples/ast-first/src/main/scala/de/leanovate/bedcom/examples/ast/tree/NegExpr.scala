package de.leanovate.bedcom.examples.ast.tree

case class NegExpr(expr: Expr) extends Expr {
  override def eval(implicit context: CalculatorContext) = -expr.eval
}
