---
layout: page
title: "Scala marcos: Getting started"
description: ""
breadcrumb: [index.md, talk/runtime_library.md, talk/generate_glue_code.md]
---

# Getting started: Hello world

{% highlight scala linenos %}
import scala.language.experimental.macros
import scala.reflect.macros.Context

object HelloWorld1Macro {
  def helloWorld() = macro helloWorld_impl

  def helloWorld_impl(c:Context)() : c.Expr[Unit] = {
    import c.universe._

    reify {
      println("Hello world")
    }
  }
}
{% endhighlight %}

{% highlight scala linenos %}
object HelloWold1 extends App {
  HelloWorld1Macro.helloWorld()
}
{% endhighlight %}

# How to debug

* Use `-Ymacro-debug-lite` option of the scala compiler
* Use `c.echo`, `c.info` ... to write your own messages to the compiler log

![Idea debug](scala_macros_idea_debug.png)

# Splicing expressions

{% highlight scala linenos %}
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
{% endhighlight %}

# Somewhat more usefull example

{% highlight scala linenos %}
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
{% endhighlight %}

{% highlight scala linenos %}
object Debug extends App {
  var a = 10

  DebugMacro.debug(a)

  DebugMacro.debug(2 * a)
}
{% endhighlight %}

~~~
Debug.this.a=10
2.*(Debug.this.a)=20
~~~
