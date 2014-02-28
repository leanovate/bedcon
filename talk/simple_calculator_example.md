---
layout: page
title: "Simple calculator example"
description: ""
breadcrumb: [index.md, talk/parser.md, talk/scala_combinators.md]
---

# Very first step: Simple addition/substraction

{% highlight scala %}
class Calculator1 extends Parsers {
  type Elem = Char

  def expr: Parser[Int] = addition | subtraction | number

  def addition: Parser[Int] = number ~ '+' ~ number ^^ { case left ~ _ ~ right => left + right }

  def subtraction : Parser[Int] = number ~ '-' ~ number ^^ { case left ~ _ ~ right => left - right }

  def number: Parser[Int] = digit.+ ^^ { digits => digits.mkString("").toInt }

  def digit: Parser[Char] = elem("digit", _.isDigit)

  def parse(str: String) = expr(new CharSequenceReader(str)) match {
    case Success(result, remain) if remain.atEnd => result
    case Success(_, remain) => throw new RuntimeException(s"Unparsed input at ${remain.pos}")
    case NoSuccess(msg, remain) => throw new RuntimeException(s"Parse error $msg at ${remain.pos}")
  }
}
{% endhighlight %}

This is already working for some basic stuff, but breaks on multiple operants:

~~~
"42"        ---> 42
"42+54"     ---> 96
"42-54"     ---> -12
"42-54+12"  ---> "Unparsed input" exception
~~~

## Pitfall 1: Longest match selection

On the very first try one might accidentally ein write the "expr" combinator in a different order:

{% highlight scala %}
...
  def expr: Parser[Int] = number | addition | subtraction
...
{% endhighlight %}

If written this way the parser behaves entirely different:

~~~
"42"        ---> 42
"42+54"     ---> "Unparsed input" exception
"42-54"     ---> "Unparsed input" exception
"42-54+12"  ---> "Unparsed input" exception
~~~

If parsers are combined by the '|' operator, the first parser in the list with a successful result wins. Since all expressions start with a number neither "addition" nor "subtraction" is taken into consideration.

As an alternative the combinator framework offers the '|||' operator, in which case all the parser the successfully consumed the most input wins (longest match combination), so the original behavior can be also achieved by:

{% highlight scala %}
...
  def expr: Parser[Int] = number ||| addition ||| subtraction
...
{% endhighlight %}

The problem with this is, that each time all combined parsers have to be evaluated, which might have a huge impact on performance for more complex grammars. As a rule of thumb it is usually better to use the '|' operator and sort the parsers accordingly.

## Pitfall 2: Recursion is not your friend

To support longer expression on is inclined to "fix" the addition and subtraction rule like this:

{% highlight scala %}
...
  def expr: Parser[Int] = addition | subtraction | number

  def addition: Parser[Int] = expr ~ '+' ~ expr ^^ { case left ~ _ ~ right => left + right }

  def subtraction : Parser[Int] = expr ~ '-' ~ expr ^^ { case left ~ _ ~ right => left - right }
...
{% endhighlight %}

When doing so the parser will break with a stack-overflow. Actually this is quite obvious, considering how the combinator framework operates, but particularly nasty if one uses a classic yacc/bison grammar as starting point (which support recursive rule definitions like this).

The stack-overflow can be avoided, when "fixing" the rules like this

{% highlight scala %}
...
  def expr: Parser[Int] = addition | subtraction | number

  def addition: Parser[Int] = number ~ '+' ~ expr ^^ { case left ~ _ ~ right => left + right }

  def subtraction : Parser[Int] = number ~ '-' ~ expr ^^ { case left ~ _ ~ right => left - right }
...
{% endhighlight %}

but does not lead to the desired result:

~~~
"42"        ---> 42
"42+54"     ---> 96
"42-54"     ---> -12
"42-54+12"  ---> -24  i.e. the expression is actually evaluated as 42 - (54 + 12)
~~~
