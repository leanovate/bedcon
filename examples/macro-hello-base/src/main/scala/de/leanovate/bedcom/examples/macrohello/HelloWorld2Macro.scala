package de.leanovate.bedcom.examples.macrohello

import scala.language.experimental.macros
import scala.reflect.macros.Context

object HelloWorld2Macro {
  def helloWorld(str: String): Int = macro helloWorld_impl

  def helloWorld_impl(c: Context)(str: c.Expr[String]): c.Expr[Int] = {
    import c.universe._

    c.echo(str.tree.pos, "Do the hello world magic")

    reify {
      println("Hello " + str.splice)
      str.splice.length
    }
  }
}
