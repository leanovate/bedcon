---
layout: page
title: "Scala marcos: Calculator example"
description: ""
breadcrumb: [index.md, talk/runtime_library.md, talk/generate_glue_code.md]
root: ..
---

# Preparation: Add function support to calculator

{% highlight scala linenos %}
trait CalculatorFunction {
  def name: String

  def call(parameters: Seq[Int]): Int
}

class CalculatorContext {
...
  private val functions = mutable.Map.empty[String, CalculatorFunction]

  def getFunction(name: String): Option[CalculatorFunction] = functions.get(name)

  def registerFunction(calculatorFunction: CalculatorFunction) = functions.put(calculatorFunction.name, calculatorFunction)
}
{% endhighlight %}

{% highlight scala linenos %}
case class CallFunctionExpr(name: String, parameters: List[Expr]) extends Expr {
  override def eval(implicit context: CalculatorContext) = context.getFunction(name).map {
    function =>
      function.call(parameters.map(_.eval))
  }.getOrElse {
    throw new RuntimeException(s"Function $name not defined")
  }
}
{% endhighlight %}

{% highlight scala linenos %}
object ASTParser extends StdTokenParsers {
...
  def term: Parser[Expr] = "(" ~> expr <~ ")" | "-" ~> expr ^^ NegExpr |
    ident ~ "(" ~ repsep(expr, ",") ~ ")" ^^ {
      case name ~ _ ~ parameters ~ _ => CallFunctionExpr(name, parameters)
    } | 
    ident ^^ VarGetExpr | 
    numericLit ^^ (str => LiteralExpr(str.toInt))
...
{% endhighlight %}

# Step 1: Very first macro that is actually compiling

{% highlight scala linenos %}
case class AdaptedFunction() extends StaticAnnotation

object FunctionAdapter {
  def generateCalculatorFunctions[T]: Seq[CalculatorFunction] = macro generateCalculatorFunctions_impl[T]

  def generateCalculatorFunctions_impl[T: c.WeakTypeTag](c: Context): c.Expr[Seq[CalculatorFunction]] = {
    import c.universe._

    reify {
      Seq()
    }
  }
}
{% endhighlight %}

# Step 2: Try to figure out how to fill a Seq dynamically

{% highlight scala linenos %}
object FunctionAdapter {
  def generateCalculatorFunctions[T]: Seq[CalculatorFunction] = macro generateCalculatorFunctions_impl[T]

  def generateCalculatorFunctions_impl[T: c.WeakTypeTag](c: Context): c.Expr[Seq[CalculatorFunction]] = {
    import c.universe._

    def createFunction(): c.Expr[CalculatorFunction] = {

      reify {
        new CalculatorFunction {
          override def name = ???

          override def call(parameters: Seq[Int]) = ???
        }
      }
    }

    val func1 = createFunction()
    val func2 = createFunction()

    reify {
      Seq(func1.splice, func2.splice)
    }
  }
}
{% endhighlight %}

~~~
scalac: Seq.apply({
  final class $anon extends CalculatorFunction {
    def <init>() = {
      super.<init>();
      ()
    };
    override def name = Predef.$qmark$qmark$qmark;
    override def call(parameters: `package`.Seq[Int]) = Predef.$qmark$qmark$qmark
  };
  new $anon()
}, {
  final class $anon extends CalculatorFunction {
    def <init>() = {
      super.<init>();
      ()
    };
    override def name = Predef.$qmark$qmark$qmark;
    override def call(parameters: `package`.Seq[Int]) = Predef.$qmark$qmark$qmark
  };
  new $anon()
})
Apply(Select(Ident(scala.collection.Seq), newTermName("apply")), List(Block(List(ClassDef(Modifiers(FINAL), newTypeName("$anon"), List(), Template(List(Ident(de.leanovate.bedcom.examples.astbase.context.CalculatorFunction)), emptyValDef, List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(), Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))), DefDef(Modifiers(OVERRIDE), newTermName("name"), List(), List(), TypeTree(), Select(Ident(scala.Predef), newTermName("$qmark$qmark$qmark"))), DefDef(Modifiers(OVERRIDE), newTermName("call"), List(), List(List(ValDef(Modifiers(PARAM), newTermName("parameters"), AppliedTypeTree(Select(Ident(scala.package), newTypeName("Seq")), List(Ident(scala.Int))), EmptyTree))), TypeTree(), Select(Ident(scala.Predef), newTermName("$qmark$qmark$qmark"))))))), Apply(Select(New(Ident(newTypeName("$anon"))), nme.CONSTRUCTOR), List())), Block(List(ClassDef(Modifiers(FINAL), newTypeName("$anon"), List(), Template(List(Ident(de.leanovate.bedcom.examples.astbase.context.CalculatorFunction)), emptyValDef, List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(), Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))), DefDef(Modifiers(OVERRIDE), newTermName("name"), List(), List(), TypeTree(), Select(Ident(scala.Predef), newTermName("$qmark$qmark$qmark"))), DefDef(Modifiers(OVERRIDE), newTermName("call"), List(), List(List(ValDef(Modifiers(PARAM), newTermName("parameters"), AppliedTypeTree(Select(Ident(scala.package), newTypeName("Seq")), List(Ident(scala.Int))), EmptyTree))), TypeTree(), Select(Ident(scala.Predef), newTermName("$qmark$qmark$qmark"))))))), Apply(Select(New(Ident(newTypeName("$anon"))), nme.CONSTRUCTOR), List()))))
~~~

# Step 3: Guess, try, guess again, try again ...

{% highlight scala linenos %}
object FunctionAdapter {
  def generateCalculatorFunctions[T]: Seq[CalculatorFunction] = macro generateCalculatorFunctions_impl[T]

  def generateCalculatorFunctions_impl[T: c.WeakTypeTag](c: Context): c.Expr[Seq[CalculatorFunction]] = {
    import c.universe._

    def createFunction(): c.Expr[CalculatorFunction] = {

      reify {
        new CalculatorFunction {
          override def name = ???

          override def call(parameters: Seq[Int]) = ???
        }
      }
    }

    val funcs = List(createFunction(), createFunction())
    c.Expr[Seq[CalculatorFunction]](Apply(Select(Ident(newTermName("Seq")), newTermName("apply")), funcs.map(_.tree)))
  }
}
{% endhighlight %}

{% highlight scala linenos %}
object FunctionAdapter {
  def generateCalculatorFunctions[T]: Seq[CalculatorFunction] = macro generateCalculatorFunctions_impl[T]

  def generateCalculatorFunctions_impl[T: c.WeakTypeTag](c: Context): c.Expr[Seq[CalculatorFunction]] = {
    import c.universe._

    val companioned = weakTypeOf[T].typeSymbol
    val companionSymbol = companioned.companionSymbol

    def createFunction(method: MethodSymbol): c.Expr[CalculatorFunction] = {

      val nameExpr = c.literal(method.name.encoded)
      reify {
        new CalculatorFunction {
          override def name = nameExpr.splice

          override def call(parameters: Seq[Int]) = ???
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
{% endhighlight %}

{% highlight scala linenos %}
    def createFunction(method: MethodSymbol): c.Expr[CalculatorFunction] = {

      val nameExpr = c.literal(method.name.encoded)

      if (method.returnType != definitions.IntTpe) {
        c.abort(method.pos, "Method has to return an Int")
      }
      method.paramss.flatten.foreach {
        param =>
          if (param.typeSignature != definitions.IntTpe) {
            c.abort(param.pos, "Only Int parameters are supported")
          }
      }

      reify {
        new CalculatorFunction {
          override def name = nameExpr.splice

          override def call(parameters: Seq[Int]) = {

            ???
          }
        }
      }
    }
{% endhighlight %}

{% highlight scala linenos %}
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
{% endhighlight %}

~~~
scalac: Seq.apply({
  final class $anon extends CalculatorFunction {
    def <init>() = {
      super.<init>();
      ()
    };
    override def name = "sum2";
    override def call(parameters: `package`.Seq[Int]) = {
      val parameterIt = parameters.iterator;
      val a = parameterIt.next();
      val b = parameterIt.next();
      BuildinFunctions.sum2(a, b)
    }
  };
  new $anon()
})
Apply(Select(Ident(newTermName("Seq")), newTermName("apply")), List(Block(List(ClassDef(Modifiers(FINAL), newTypeName("$anon"), List(), Template(List(Ident(de.leanovate.bedcom.examples.astbase.context.CalculatorFunction)), emptyValDef, List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(), Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))), DefDef(Modifiers(OVERRIDE), newTermName("name"), List(), List(), TypeTree(), Literal(Constant("sum2"))), DefDef(Modifiers(OVERRIDE), newTermName("call"), List(), List(List(ValDef(Modifiers(PARAM), newTermName("parameters"), AppliedTypeTree(Select(Ident(scala.package), newTypeName("Seq")), List(Ident(scala.Int))), EmptyTree))), TypeTree(), Block(List(ValDef(Modifiers(), newTermName("parameterIt"), TypeTree(), Select(Ident(newTermName("parameters")), newTermName("iterator"))), ValDef(Modifiers(), newTermName("a"), TypeTree(), Apply(Select(Ident(newTermName("parameterIt")), newTermName("next")), List())), ValDef(Modifiers(), newTermName("b"), TypeTree(), Apply(Select(Ident(newTermName("parameterIt")), newTermName("next")), List()))), Apply(Select(Ident(de.leanovate.bedcom.examples.astexample.BuildinFunctions), newTermName("sum2")), List(Ident(newTermName("a")), Ident(newTermName("b")))))))))), Apply(Select(New(Ident(newTypeName("$anon"))), nme.CONSTRUCTOR), List()))))
~~~
