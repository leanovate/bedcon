---
layout: page
title: "Missing lexer modes"
description: ""
breadcrumb: [index.md, talk/parser.md, talk/scala_combinators.md]
---

As discussed in the previous section the scala combinator framework is quite powerful and already offers a generic support for lexical anaylzers. Unluckily this does not suffice for PHP, since there is no support for lexer modes.

# What's a lexer mode

In classical lexer generators modes are a feature to tag rules, i.e. to turn on and off certain rules depending on the mode the lexer currently operates in. Usually lexer modes can be used in form of a stack, i.e. you might push a new mode to the stack and eventually pop back to the original mode.

An often used example of lexer modes are comments: If the lexer encounters a `/*` it switches to an "in comment" mode where everything is ignored and pop back to its previous mode once an `*/` is encountered.

# The many lexer modes of PHP

Since lexer modes are basically just selectors for rules, it is always possible to rewrite any lexer to operate in a single mode. After all scala's `StdLexer` is perfectly capable of handling `/* ... */` style comments without relying on lexer modes. The drawback to this, that the removal the lexer modes may lead to a vast duplication of rules and a closer look at the original PHP lexer reveals that it has 10 modes in total:

* INITIAL
* IN_SCRIPTING
* DOUBLE_QUOTES
* BACKQUOTE
* HEREDOC
* END_HEREDOC
* NOWDOC
* VAR_OFFSET
* LOOKING_FOR_PROPERTY
* LOOKING_FOR_VARNAME

Most of these are demonstrated in this simple example:

![PHP's lexer modes](lexer_modes.png)

Combining all there to a single rule set might be possible, but wont be pretty.

# Lexer modes with scala combinators

