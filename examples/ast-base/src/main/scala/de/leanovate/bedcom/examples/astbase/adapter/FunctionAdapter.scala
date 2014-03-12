package de.leanovate.bedcom.examples.astbase.adapter

import de.leanovate.bedcom.examples.astbase.context.CalculatorFunction
import scala.reflect.macros.Context
import scala.language.experimental.macros

object FunctionAdapter {
  def generateCalculatorFunctions(inst: Any): Seq[CalculatorFunction] = macro generateCalculatorFunctions_impl

  def generateCalculatorFunctions_impl(c: Context)(inst: c.Expr[Any]): c.Expr[Seq[CalculatorFunction]] = {
    import c.universe._

    reify {
      Seq()
    }
  }
}
