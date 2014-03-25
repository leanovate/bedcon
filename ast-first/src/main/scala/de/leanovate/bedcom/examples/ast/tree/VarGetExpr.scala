package de.leanovate.bedcom.examples.ast.tree

case class VarGetExpr(name: String) extends Expr {
  override def eval(implicit context: CalculatorContext) = context.getVariable(name).getOrElse {
    throw new RuntimeException(s"Variable $name not defined")
  }
}
