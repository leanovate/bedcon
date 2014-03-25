package de.leanovate.bedcom.examples.astexample

import de.leanovate.bedcom.examples.astbase.context.CalculatorContext
import de.leanovate.bedcom.examples.astbase.parser.ASTParser

object Calculator {

  def createContext = {
    val context = new CalculatorContext

    BuildinFunctions.calculatorFunctions.foreach(context.registerFunction)
    context
  }

  def calculate(str: String) = {

    val expr = ASTParser.parse(str)
    expr.eval(createContext)
  }

  def main(args: Array[String]) {

    println(calculate("sum2(1,2)"))

  }
}
