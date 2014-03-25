package de.leanovate.bedcom.examples.macrohello

import scala.language.experimental.macros
import scala.reflect.macros.Context

object DebugMacro {
  def debug(a: Any) = macro debug_impl

  def debug_impl(c: Context)(a: c.Expr[Any]) = {
    import c.universe._

    val dump = show(a.tree)
    val dumpExpr = c.literal(dump)

    reify {
      println(dumpExpr.splice + "=" + a.splice.toString)
    }
  }
}
