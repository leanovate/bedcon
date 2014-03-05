package de.leanovate.bedcom.examples.ast.tree

case class LiteralExpr(value: Int) extends Expr {
  override def eval(implicit context: CalculatorContext) = value

}
