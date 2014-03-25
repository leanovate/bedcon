---
layout: page
title: "Generate glue code using Scala macros"
description: ""
breadcrumb: [index.md, talk/runtime_library.md]
---

# The problem

Regular scala functions and PHP functions do not fit well which each other:

* Scala functions are strictly typed while PHP functions are usually untyped or "not completely" typed.
  * Since PHP 5 it is possible to declare type hints for function parameters, but this only applies for classes, interfaces, arrays and callables. There is no way to distinguished a string parameter from an integer parameter.
* PHP functions support call-by references, while scala functions usually take immutable parameters.
  * Of course it is possible to use "holder" objects for results, but his is not considered clean code.
* In scala the parameter match of a function call is always evaluated at compile time, in PHP it is evaluated at interpretation time.

# Example

At the moment the PHP function trait in JBJ looks like this:

{% highlight scala linenos %}
trait PFunction {
  def name: NamespaceName

  def parameters: Seq[PParamDef]

  def call(parameters: List[PParam])(implicit callerCtx: Context): PAny
}
{% endhighlight %}

* `name` is the name of the function as it should be used in a function call. Since the function be defined in a namespace this is a `NamespaceName` instead of just a string.
* `parameters` list of parameter definitions including type hints if present
* `call` the actual invocation of the function with concrete parameters. `PParam` serves as a wrapper if the parameter was given as concrete value of by reference.

Consider one wants to implement the buildin function [strncmp](http://www.php.net/manual/de/function.strncmp.php). It would be quite inconvenient to implement the `PFunction` trait itself, instead one would like to just implement it as regular scala function like this

{% highlight scala linenos %}
object StringFunctions {
  def strncmp(str1: String, str2: String, len: Int): Int = {
    str1.take(len).compareTo(str2.take(len))
  }
}
{% endhighlight %}

First we need a generic helper to extract parameters with PHP compatible error handling.

{% highlight scala linenos %}
trait ParameterAdapter[T] {
  def adapt(parameters: Iterator[PParam])(implicit ctx: Context): T
}
{% endhighlight %}

* `parameters` is the iterator of the parameters that need to be processed. The adapter is supposed to take one or more parameters from the iterator (or throw some error)
* The result is a tuple of the extracted scala value and a list of remaining parameters to be forwarded to the next `ParameterAdapter`.

Now we need some glue code to convert this function to a `PFunction` usable by the interpreted PHP, which might look like this:

{% highlight scala linenos %}
new PFunction {
  def name = NamespaceName("strncmp")

  def parameters = Seq(
    AdaptedParamDef("str1", None, false, None),
    AdaptedParamDef("str2", None, false, None),
    AdaptedParamDef("len", None, false, None)
  )

  private val errorHandlers = ParameterAdapter.errorHandlers("strncmp", ParameterMode.EXACTLY_WARN, 3, 3, false, NullVal)

  private val adapters = (
    RelaxedParamterAdapter(0, StringConverter, errorHandlers),
    RelaxedParamterAdapter(1, StringConverter, errorHandlers),
    RelaxedParamterAdapter(2, IntConverter, errorHandlers)
  )

  def doCall(parameters: List[PParam])(implicit callerCtx: Context): PAny = {
    if (parameters.size > 3)
      errorHandlers.tooManyParameters(parameters.size)

    val parametersIt = parameters.iterator
    val param1 = adapters._1.adapt(parametersIt)
    val param2 = adapters._2.adapt(parametersIt)
    val param3 = adapters._3.adapt(parametersIt)
    val result = StringFunctions.strncmp(param1, param2, param3)
    PValConverter.toJbj(result)(callerCtx)
  }
}
{% endhighlight %}

Of course it is extremely cumbersome to write all this by hand for each function. I.e. one would like to have a function like this:

{% highlight scala linenos %}
  def generatePFunctions(instance: Any): Seq[PFunction]
{% endhighlight %}

* `instance` is the instance/object to be scanned for scala functions, which should be decorated by a `PFunction` as shown above.

The usual Java-approach would be to "generate" this kind of glue-code via reflection and (potentially) AOP. In Scala it is possible to use type-safe marcos for this kind of work.

* [Scala macros: Getting started](scala_macros.html)
* [Scala macros: Calculator example](scala_macros_calculator.html)

