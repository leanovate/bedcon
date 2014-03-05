package de.leanovate.bedcom.examples.ast.tree

trait Expr extends Node {
  def eval(implicit context: CalculatorContext): Int
}
