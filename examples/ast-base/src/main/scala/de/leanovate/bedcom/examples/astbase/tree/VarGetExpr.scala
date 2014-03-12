package de.leanovate.bedcom.examples.astbase.tree

import de.leanovate.bedcom.examples.astbase.context.{UndefinedVariableException, CalculatorContext}

case class VarGetExpr(name: String) extends Expr {
  override def eval(implicit context: CalculatorContext) = context.getVariable(name).getOrElse {
    throw new UndefinedVariableException(name)
  }
}
