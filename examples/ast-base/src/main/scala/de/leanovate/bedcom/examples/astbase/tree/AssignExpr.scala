package de.leanovate.bedcom.examples.astbase.tree

import de.leanovate.bedcom.examples.astbase.context.CalculatorContext

case class AssignExpr(name: String, expr: Expr) extends Expr {
  override def eval(implicit context: CalculatorContext) = {

    val value = expr.eval
    context.setVariable(name, value)
    value
  }
}
