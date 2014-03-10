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

{% highlight scala %}
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

{% highlight scala %}
  def strncmp(str1: String, str2: String, len: Int): Int = {
    str1.take(len).compareTo(str2.take(len))
  }
{% endhighlight %}

Now we need some glue code to convert this function to a `PFunction` usable by the interpreted PHP, which might look like this:

{% highlight scala %}
  new PFunction {
    def name = NamespaceName("strncmp")
    def parameters = Seq(AdaptedParamDef("str1", None, false, None), AdaptedParamDef("str2", None, false, None), AdaptedParamDef("len", None, false, None))
    def call(parameters: List[PParam])(implicit callerCtx: Context): PAny = {
      if (parameters.size.$greater(3)) {
        callerCtx.log.warn(s"strncmp() expects exactly 3 parameters, ${parameters.size} given")
        return NullVal
      }
      val param0 = DefaultParamterAdapter(StringConverter).adapt(parameters, false, {
        callerCtx.log.warn(s"strncmp() expects exactly 3 parameters, ${parameters.size} given")
        return NullVal
      }, (expectedTypeName: String, givenTypeName: String) => ())
      val param1 = DefaultParamterAdapter.apply(StringConverter).adapt(param0._2, false, {
        callerCtx.log.warn(s"strncmp() expects exactly 3 parameters, ${parameters.size} given")
        return NullVal
      }, (expectedTypeName: String, givenTypeName: String) => ())
      val param2 = DefaultParamterAdapter.apply(IntConverter).adapt(param1._2, false, {
        callerCtx.log.warn(s"strncmp() expects exactly 3 parameters, ${parameters.size} given")
        return NullVal
      }, (expectedTypeName: String, givenTypeName: String) => ())
      val result = StringFunctions.strncmp(param0._1, param1._1, param2._1)
      PValConverter.toJbj(result)(callerCtx)
  }
}
{% endhighlight %}
