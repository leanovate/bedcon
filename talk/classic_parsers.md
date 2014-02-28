---
layout: page
title: "Short overview over classic lexers/parsers"
description: ""
breadcrumb: [index.md, talk/parser.md]
---

# What's a lexical analyzer aka scanner aka lexer

A lexical analyzer is supposed to handle the lexical parsing (word analysis) of a programming language.

More specifially it is supposed the handle ...

* ... comments (usually just ignoring them)
* ... whitespaces (usually ignoring them as well)
* ... special characters
  * For Java: '{', '}', '[', ']', '(', ')', '.', ';', ':', '=', '+', '-', ...
* ... language keywords
  * For Java: abstract, assert, boolean, break, byte, case, catch, char, class, const, continue, default, do, double, else, enum, extends, 
    final, finally, float, for, goto, if, implements, import, instanceof, int, long, native, new, package, private, protected, public, return, short, static,
    strictfp, super, switch, synchronized, this, throw, throws, transient, try, void, volatile, while
* ... identifiers
  * Variable names
  * Function names
  * Class names
  * ....
* ... literals
  * Double-quoted strings: "Hello"
  * Single-quoted string/character: 'H'
  * Numbers: 42, 23.01, 1e6, 0x1e, 1.0f, 0123
  * Reserved words: false, null, true

If the lexical structure of a program is correct the lexical analyzer will convert the source code of a program to a parser-specific sequence of tokens.

Java-like example:

{% highlight java %}
class MyClass {
    void doIt (  ) {
        System.out.println("Hello " + 42);
    }
}
{% endhighlight %}

{% highlight java %}
Keyword("class"), Identifier("MyClass"), Char('{'),
  Keyword("void"), Identifier("doIt"), Char('('), Char(')'), Char('{'),
    Identifier("System"), Char('.'), Identifier("out"), Char('.'), 
      Identifier("println"), Char('('), StringLiteral("Hello "), Char('+'), IntLiteral(42), Char(')'), Char(';'),
  Char('}'),
Char('}')
{% endhighlight %}

Classically lexical analyzers are generated using the UNIX "lex" tool, which is actually part of the POSIX standard. The originally version of "lex" was closed source (AT&T).
Todays de-facto standard is the open-source version [flex](http://flex.sourceforge.net/) (which is not based on the original code and actually not part of GNU).

# What's a parser

A parser is supposed to perform a syntactic analysis of the token stream provided by the lexer and generate the desired output, which may be ...

* ... concrete machine code (C, C++, ...)
* ... byte-code for a VM (Java, PHP/Zend, ...)
* ... an abstract syntax tree for interpretation or further processing

Classically parsers are generated using the UNIX "yacc" tool, which is part of the POSIX standard as well. Like "lex" the original version of "yacc" was closed source (AT&T).
Todays de-facto standards are GNU's [bison](http://www.gnu.org/software/bison/) or Berkley's [byacc](http://invisible-island.net/byacc/byacc.html).
