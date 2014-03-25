package de.leanovate.bedcom.examples.ast.parser

import de.leanovate.bedcom.examples.ast.tree.CalculatorContext

object Calculator {
  def calculate(str: String) = {

    val expr = ASTParser.parse(str)
    expr.eval(new CalculatorContext)
  }
}
