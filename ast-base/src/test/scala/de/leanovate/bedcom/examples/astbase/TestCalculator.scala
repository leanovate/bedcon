package de.leanovate.bedcom.examples.astbase

import de.leanovate.bedcom.examples.astbase.context.{CalculatorFunction, CalculatorContext}
import de.leanovate.bedcom.examples.astbase.parser.ASTParser

object TestCalculator {

  object OneFunction extends CalculatorFunction {
    override def name = "one"

    override def call(parameters: Seq[Int]) = 1
  }

  object SumFunction extends CalculatorFunction {
    override def name = "sum"

    override def call(parameters: Seq[Int]) = {

      parameters.foldLeft(0) {
        (sum, elem) => sum + elem
      }
    }
  }

  def createContext = {

    val context = new CalculatorContext

    context.registerFunction(OneFunction)
    context.registerFunction(SumFunction)
    context
  }

  def calculate(str: String) = {

    val expr = ASTParser.parse(str)
    expr.eval(createContext)
  }
}
