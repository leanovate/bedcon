package de.leanovate.bedcom.examples.astbase.tree

import de.leanovate.bedcom.examples.astbase.context.CalculatorContext

trait Expr extends Node {
  def eval(implicit context: CalculatorContext): Int
}
