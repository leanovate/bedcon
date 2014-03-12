package de.leanovate.bedcom.examples.astbase.tree

import de.leanovate.bedcom.examples.astbase.context.CalculatorContext

case class LiteralExpr(value: Int) extends Expr {
  override def eval(implicit context: CalculatorContext) = value

}
