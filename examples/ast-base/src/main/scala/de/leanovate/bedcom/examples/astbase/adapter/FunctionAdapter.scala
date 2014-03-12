package de.leanovate.bedcom.examples.astbase.adapter

import de.leanovate.bedcom.examples.astbase.context.CalculatorFunction
import scala.reflect.macros.Context
import scala.language.experimental.macros

object FunctionAdapter {
  def generateCalculatorFunctions[T](inst: T): Seq[CalculatorFunction] = macro generateCalculatorFunctions_impl[T]

  def generateCalculatorFunctions_impl[T](c: Context)(inst: c.Expr[T]): c.Expr[Seq[CalculatorFunction]] = {
    import c.universe._

    def createFunction(method: MethodSymbol): c.Expr[CalculatorFunction] = {

      val nameExpr = c.literal(method.name.encoded)
      reify {
        new CalculatorFunction {
          override def name = nameExpr.splice

          override def call(parameters: Seq[Int]) = ???
        }
      }
    }

    val funcs = inst.actualType.members.filter {
      member =>
        member.isMethod && member.annotations.exists(_.tpe == typeOf[AdaptedFunction])
    }.map {
      member =>
        createFunction(member.asMethod)
    }.toList
    c.Expr[Seq[CalculatorFunction]](Apply(Select(Ident(newTermName("Seq")), newTermName("apply")), funcs.map(_.tree)))
  }
}
