package de.leanovate.bedcom.examples.ast.tree

import scala.collection.mutable

class CalculatorContext {
  private val variables = mutable.Map.empty[String, Int]

  def getVariable(name: String): Option[Int] = variables.get(name)

  def setVariable(name: String, value: Int) = variables.put(name, value)
}
