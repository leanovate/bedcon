package de.leanovate.bedcom.examples.astexample

import de.leanovate.bedcom.examples.astbase.adapter.{FunctionAdapter, AdaptedFunction}

trait BuildinFunctions {
  @AdaptedFunction
  def sum2(a: Int, b: Int): Int = a + b
}

object BuildinFunctions extends BuildinFunctions {
  val calculatorFunctions = FunctionAdapter.generateCalculatorFunctions[BuildinFunctions]
}

