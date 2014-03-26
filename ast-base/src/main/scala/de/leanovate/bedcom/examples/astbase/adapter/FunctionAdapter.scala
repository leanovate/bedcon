package de.leanovate.bedcom.examples.astbase.adapter

import de.leanovate.bedcom.examples.astbase.context.CalculatorFunction
import scala.reflect.macros.Context
import scala.language.experimental.macros

object FunctionAdapter {
  def generateCalculatorFunctions[T]: Seq[CalculatorFunction] = macro generateCalculatorFunctions_impl[T]

  def generateCalculatorFunctions_impl[T: c.WeakTypeTag](c: Context): c.Expr[Seq[CalculatorFunction]] = {
    import c.universe._

    val companioned = weakTypeOf[T].typeSymbol
    val companionSymbol = companioned.companionSymbol

    def createFunction(method: MethodSymbol): c.Expr[CalculatorFunction] = {

      val nameExpr = c.literal(method.name.encoded)

      if (method.returnType != definitions.IntTpe) {
        c.abort(method.pos, "Method has to return an Int")
      }
      val parameterNames = method.paramss.flatten.map {
        param =>
          if (param.typeSignature != definitions.IntTpe) {
            c.abort(param.pos, "Only Int parameters are supported")
          }
          param.name.toTermName
      }

      val parameterItName = newTermName("parameterIt")
      val parameterItDecl = ValDef(Modifiers(), parameterItName, TypeTree(),
                                    Select(Ident(newTermName("parameters")), newTermName("iterator")))
      val parameterItNext = Apply(Select(Ident(parameterItName), newTermName("next")), Nil)
      val decls = parameterNames.map {
        parameterName =>
          ValDef(Modifiers(), parameterName, TypeTree(), parameterItNext)

      }

      val call = Apply(Select(Ident(companionSymbol), method.name.toTermName), parameterNames.map(Ident(_)))
      val callImpl = c.Expr[Int](Block(parameterItDecl :: decls, call))
      reify {
        new CalculatorFunction {
          override def name = nameExpr.splice

          override def call(parameters: Seq[Int]) = callImpl.splice
        }
      }
    }


    val funcs = companioned.typeSignature.members.filter {
      member =>
        member.isMethod && member.annotations.exists(_.tpe == typeOf[AdaptedFunction])
    }.map {
      member =>
        createFunction(member.asMethod)
    }.toList
    c.Expr[Seq[CalculatorFunction]](Apply(Select(Ident(newTermName("Seq")), newTermName("apply")), funcs.map(_.tree)))
  }
}
