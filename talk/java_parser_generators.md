---
layout: page
title: "Java parser generators"
description: ""
breadcrumb: [index.md, talk/parser.md]
---

* [JavaCC](https://javacc.java.net/)
  * Combines the functionality of a lexer and parser
  * Contains helpers to generate abstract syntax trees
  * Can generate BNF (Backus-Naur-Form) documentation of a gammar
* [ANTLR](http://www.antlr.org/)
  * Similiar feature set
  * May use a diffent lexer than its own
  * Used by Jython
* [JLex](http://www.cs.princeton.edu/~appel/modern/java/JLex/)
  * Lexical analyzers generator for Java
  * Supposed to be the Java version of "lex" (for Java in Java)
* [JFlex](http://jflex.de/)
  * Successor of JLex
  * Used by JRuby
* [CUP](http://www.cs.princeton.edu/~appel/modern/java/CUP/)
  * Parser generator for Java
  * Supposed to be used in combination with JLex
* [BYacc/J](http://byaccj.sourceforge.net/)
  * Extension of byacc to generate Java code instead of C
  * Actually written in C
* [Jay](https://github.com/jruby/jay)
  * Adopted by JRuby
  * Actually written in C
  * Based on byacc, but uses templates/skeletons to generate its outputs
  * Skeletons for Java and C# are provided
* [parboiled](https://github.com/sirthias/parboiled)
  * Not a classic generator but a library to write parsers
  * Bindings for Java and Scala
* ...

