---
layout: page
title: "Scala combinators"
description: ""
breadcrumb: [index.md, talk/parser.md]
---

Scala combinators are part of the scala-library (scala.util.parsing.combinator) and offer a new way to write lexical analyzers and parsers. In contrast to to the classic approach a lexer is supposed to be just another kind of parser that can be written with the same tool set as the parser itself.

Very simplistic overview of the interface

{% highlight scala %}
package scala.util.parsing.combinator

trait Parsers {
    type Elem

    trait Parser {
        def apply(input: Reader[Elem]) : ParseResult[T]

        ...
    }

    sealed abstract class ParseResult[+T]

    case class Success[+T](...) extends ParseResult[T]

    sealed abstract class NoSuccess(...)

    case class Failure(...) extends NoSuccess(...)

    case class Error(...) extends NoSuccess(...)

    ...
}
{% endhighlight %}

{% highlight scala %}
abstract class Reader[+T] {
  def first: T

  def rest: Reader[T]

  def pos: Position

  def atEnd: Boolean

  ...  
}
{% endhighlight %}


For simple parsers it usually suffices to use Parsers.Elem = Char. Though it is also possible to use the combinator framework in a more classic a approach by implementing a lexical analyzer with Parsers.Elem = Char and ParseResult[Token] beneath a syntactical parser with Parsers.Elem = Token and ParseResult[ASTNode].

As a starting point the framework already contains pre-defined base classes that support Java-like languages:

* scala.util.parsing.combinator.syntactical.StdTokenParsers which is using
  * scala.util.parsing.combinator.lexical.StdLexical as lexer which is producing tokens defined in
  * scala.util.parsing.combinator.token.StdToken

This should be discussed in more detail with the classic [Calculator example](scala_calculator_example.html).

Unluckily these classes do not contain a feasible pattern for different lexer modes, which important to parser PHP: [Missing lexer modes](missing_lexer_modes.html)