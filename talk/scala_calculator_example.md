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

  def subtraction: Parser[Int] = number ~ '-' ~ number ^^ { case left ~ _ ~ right => left - right }

  def number: Parser[Int] = digit.+ ^^ { digits => digits.mkString("").toInt }

  def digit: Parser[Char] = elem("digit", ch => ch.isDigit)

  def parse(str: String):Int = expr(new CharSequenceReader(str)) match {
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

In depth description of the combinations used in this example:

* `elem(kind: String, condition: Elem => Boolean)` is a helper to create a simple parser that consumes a single element `Elem` and is successful if the element matches a given condition. In the example it is used to create a parser that consumes a digit character.
* The `+` postfix is a shorthand for `rep1[T](p: => Parser[T]): Parser[List[T]]` with creates a new parser by repeating a given parser as long as it is successful, whereas the new parser is only successful if the repeated parser is only successful at least once. In the example this is used to combine the `digit` parser to a list of digits parser.
* The `^^` operator is a shorthand of `map[U](f: T => U): Parser[U]` which creates a new parser be converting the result of a given parser. In the example this is used to convert the lost of digits parsers to the `number` parser and to perform the actual calculations.
* The `~` operator creates a new parser by combining two given parsers, the new parser is only successful if both parsers are successful in succession. I.e. if `a` and `b` are parsers, `a ~ b` is successful if first `a` successfully consumes some input and then `b` consumes some of the remaining input. This is the building block to define syntactical rules.
* The `|` operator is a shorthand for `append[U >: T](p: => Parser[U]): Parser[U]` which also creates a new parser by combining two given parsers, whereas the new parser succeeds if one of the given parsers is successful on the same input. I.e. if `a` and `b` are parsers, `a | b` is successful if `a` successfully consumes some input or if `a` fails and `b` successfully consumes some input.
* Finally it should be mentioned that the `Parsers` trait contains an implicit conversion, so that a single element (or character in the example) to a parser consuming exactly this element. I.e. `'+'` is implicitly converted to a parser consuming the + sign.

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

  def subtraction: Parser[Int] = number ~ '-' ~ expr ^^ { case left ~ _ ~ right => left - right }
...
{% endhighlight %}

but does not lead to the desired result:

~~~
"42"        ---> 42
"42+54"     ---> 96
"42-54"     ---> -12
"42-54+12"  ---> -24  i.e. the expression is actually evaluated as 42 - (54 + 12)
~~~

Also, if the input is long enough (i.e. contains many '+' and '-' operations), the parser might break with a stack-overflow again, which makes it unusable in a production environment.

# Support multiple operations and operator precedence

The combinator framework offers various tools to handle the concatenation off operators efficiently, though somewhat different from the way in classical grammars. Instead of writing a set of recursive rules it is possible to express a chain of parsers separated by delimiters/operator and define how the results should be combined for each delimiter/operator.

{% highlight scala %}
class Calculator2 extends Parsers {
  type Elem = Char

  def expr = addSub

  def addSub: Parser[Int] = mulDiv * (
      '+' ^^^ { (left: Int, right: Int) => left + right} 
    | '-' ^^^ { (left: Int, right: Int) => left - right} )

  def mulDiv = number * (
      '*' ^^^ { (left: Int, right: Int) => left * right } 
    | '/' ^^^ { (left: Int, right: Int) => left / right } )

  def number = digit.+ ^^ { digits => digits.mkString("").toInt }

  def digit = elem("digit", _.isDigit)
}
{% endhighlight %}

Which already honor the precedence of '*' and '/' over '+' and '-', i.e. it already works like a real calculator:

~~~
"42"              ---> 42
"42+54"           ---> 96
"42-54"           ---> -12
"42-54+12"        ---> 0
"42-3*3*3*2+24/2" ---> 0
~~~

## Pitfall 3: Pollution of the syntax definition

Even though the previous example already works quite well, it has no support for whitespace yet. I.e. "42" works perfectly well, while "  42  " already breaks. To fix this one might be inclined to simple extend the `number` parser like this:

{% highlight scala %}
...
  def number = whitespace.* ~> digit.+ <~ whitespace.* ^^ { digits => digits.mkString("").toInt }

  def digit = elem("digit", _.isDigit)

  def whitespace = elem("whitespace", ch => ch == ' ' || ch == '\t')
...
{% endhighlight %}

* The `*` postfix is a shorthand for `rep[T](p: => Parser[T]): Parser[List[T]]` which - like the `+` postfix - also repeats a given parser as, though it is also successful if there are no matches at all.
* The `~>` and `<~` are variants of the `~` operator if the result of the left resp. right parser is of no interest and should be dropped.

Event though this fixes the whitespace problem and might work for a simple example like this, one can imagine that a grammar will become quickly polluted if whitespace rules (and other lexical patterns) are interwoven at all places where there might occur.

# Use the classic lexer/parser pattern

To prevent a pollution of the grammar it is usually a good idea to split the parser into a lexical analyzer and a syntactic parser, just like one would do with lex/flex and yacc/bison in the classic world. To support this, the combinator framework already contains a lexical analyzer that might be used out of the box in many cases:

{% highlight scala %}
class Calculator3 extends StdTokenParsers {
  override type Tokens = StdLexical

  override val lexical = new StdLexical

  lexical.delimiters ++= List("(", ")", "+", "-", "*", "/")

  def expr: Parser[Int] = addSub

  def addSub: Parser[Int] = mulDiv * (
      '+' ^^^ { (left: Int, right: Int) => left + right} 
    | '-' ^^^ { (left: Int, right: Int) => left - right} )

  def mulDiv = number * (
      '*' ^^^ { (left: Int, right: Int) => left * right } 
    | '/' ^^^ { (left: Int, right: Int) => left / right } )

  def term: Parser[Int] = "(" ~> expr <~ ")" | numericLit ^^ (_.toInt)

  def parse(str: String) = expr(new lexical.Scanner(str)) match {
    case Success(result, remain) if remain.atEnd => result
    case Success(_, remain) => throw new RuntimeException(s"Unparsed input at ${remain.pos}")
    case NoSuccess(msg, remain) => throw new RuntimeException(s"Parse error $msg at ${remain.pos}")
  }
}
{% endhighlight %}

As one can see the grammar is almost unchanged while there is now support for whitespaces and comments.

~~~
"42 - 3*3*3*2 + 24/2"                      ---> 0
""2 * (3 /* blah blah */ +4) / 2 + 7 * 5"" ---> 42
~~~

From this point on it becomes somewhat straight forward to extends this simple example by several other operators, support for functions or even to a full fledged programming language in itself. As an introduction to the combinator framework there is no need to stretch this any further than this.

