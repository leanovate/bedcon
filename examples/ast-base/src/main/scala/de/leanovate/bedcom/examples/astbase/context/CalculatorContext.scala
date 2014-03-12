package de.leanovate.bedcom.examples.astbase.context

import scala.collection.mutable

class CalculatorContext {
  private val variables = mutable.Map.empty[String, Int]

  private val functions = mutable.Map.empty[String, CalculatorFunction]

  def getVariable(name: String): Option[Int] = variables.get(name)

  def setVariable(name: String, value: Int) = variables.put(name, value)

  def getFunction(name: String): Option[CalculatorFunction] = functions.get(name)

  def registerFunction(calculatorFunction: CalculatorFunction) = functions
    .put(calculatorFunction.name, calculatorFunction)
}
