---
layout: page
title: "Missing lexer modes"
description: ""
breadcrumb: [index.md, talk/parser.md, talk/scala_combinators.md]
---

As discussed in the previous section the scala combinator framework is quite powerful and already offers a generic support for lexical anaylzers. Unluckily this does not suffice for PHP, since there is no support for lexer modes.

# What's a lexer mode

Lexer modes are a common feature of the classic tools to generate lexers that are able to run in different modes.

Consider the following example:

![Lexer mode example](lexer_modes1.png)

As one can already see in this simple example a PHP programm contains varios sections 