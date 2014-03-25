package de.leanovate.bedcom.examples.astbase.context

trait CalculatorFunction {
  def name: String

  def call(parameters: Seq[Int]): Int
}
