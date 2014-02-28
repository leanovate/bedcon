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

~~~
"42"        ---> 42
"42+54"     ---> 96
"42-54"     ---> -12
"42-54+12"  ---> "Unparsed input" exception
~~~