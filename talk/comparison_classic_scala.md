---
layout: page
title: "Comparison: flex/bison vs Scala combinators"
description: ""
breadcrumb: [index.md, talk/parser.md]
---

# Lexer side-by-side

Actually a side-by-side comparison of flex and a lexer based one scala combinators is next to impossible. Let's take a look at some of PHP's flex code and its corresponding part in JBJ:

<div class="container">
<div class="col-xs-6">
{% highlight c %}
...
<ST_IN_SCRIPTING>"new" {
    return T_NEW;
}

<ST_IN_SCRIPTING>"clone" {
    return T_CLONE;
}

<ST_IN_SCRIPTING>"var" {
    return T_VAR;
}

<ST_IN_SCRIPTING>"("{TABS_AND_SPACES}("int"|
  "integer"){TABS_AND_SPACES}")" {
    return T_INT_CAST;
}

<ST_IN_SCRIPTING>"("{TABS_AND_SPACES}("real"|
  "double"|"float"){TABS_AND_SPACES}")" {
    return T_DOUBLE_CAST;
}

<ST_IN_SCRIPTING>"("{TABS_AND_SPACES}("string"|
  "binary"){TABS_AND_SPACES}")" {
    return T_STRING_CAST;
}

<ST_IN_SCRIPTING>"("{TABS_AND_SPACES}"array"
  {TABS_AND_SPACES}")" {
    return T_ARRAY_CAST;
}
...
{% endhighlight %}
</div>

<div class="col-xs-6">
{% highlight scala %}
case class ScriptLexer(mode: ScriptingLexerMode) 
                extends Lexer {
...
val reserved = Set( ... "var", "new", "clone", ... )

def processIdent(name: String) =
    if (reserved contains name.toLowerCase) 
      Keyword(name.toLowerCase) 
    else 
      Identifier(name)
...
'(' ~> tabsOrSpaces ~> (str("int") | str("integer")) 
    <~ tabsOrSpaces <~ ')' ^^ {
      s => IntegerCast(s) } 
| '(' ~> tabsOrSpaces ~> (str("real") | str("double")
        | str("float")) <~ tabsOrSpaces <~ ')' ^^ {
      s => DoubleCast(s) }
| '(' ~> tabsOrSpaces ~> (str("string") | 
         str("binary")) <~ tabsOrSpaces <~ ')' ^^ {
      s => StringCast(s) } 
| '(' ~> tabsOrSpaces ~> str("array") 
    <~ tabsOrSpaces <~ ')' ^^ {
      s => ArrayCast(s) } 
| '(' ~> tabsOrSpaces ~> str("bool") 
    <~ opt(str("ean")) <~ tabsOrSpaces <~ ')' ^^ {
      s => BooleanCast(s) } 
| '(' ~> tabsOrSpaces ~> str("unset") 
    <~ tabsOrSpaces <~ ')' ^^ {
      s => UnsetCast(s) }
...
}
{% endhighlight %}
</div>
</div>

Overall there are some similarities, but the syntax is vastly different so that a one-by-one translation of the rules is in general on a feasible approach.

# Parser side-by-side

The transformation of yacc rules to corresponding scala combinators is more straight-forward, as long as the the underlying lexical analyzers have a similar set of tokens. Let's take a look at some off PHP's yacc/bison code and the corresponding parts in JBJ:

<div class="container">
<div class="col-xs-6">
{% highlight c %}
...
interface_extends_list:
        /* empty */
    |   T_EXTENDS interface_list
;

implements_list:
        /* empty */
    |   T_IMPLEMENTS interface_list
;

interface_list:
        fully_qualified_class_name {...}
    |   interface_list ',' fully_qualified_class_name {...}
;

foreach_optional_arg:
        /* empty */                     {...}
    |   T_DOUBLE_ARROW foreach_variable {...}
;

foreach_variable:
        variable            {...}
    |   '&' variable        {...}
    |   T_LIST '(' {...} assignment_list ')' {...}
;

for_statement:
        statement
    |   ':' inner_statement_list T_ENDFOR ';'
;


foreach_statement:
        statement
    |   ':' inner_statement_list T_ENDFOREACH ';'
;
...
{% endhighlight %}
</div>

<div class="col-xs-6">
{% highlight scala %}
class JbjParser extends Parsers with PackratParsers {
...
lazy val interfaceExtendsList = 
  opt("extends" ~> interfaceList) ^^ { ... }

lazy val implementsList = 
  opt("implements" ~> interfaceList) ^^ { ... }

lazy val interfaceList = 
  rep1sep(fullyQualifiedClassName, ",")

lazy val foreachOptionalArg = 
  opt("=>" ~> foreachVariable)

lazy val foreachVariable =
  variable ^^ { ... } | 
  "&" ~> variable ^^ { ... } | 
  "list" ~> "(" ~> assignmentList <~ ")" ^^ { ... }

lazy val forStatement =
  ":" ~> innerStatementList <~ "endfor" <~ ";" | 
  statement ^^ (List(_)) | 
  ";" ^^^ Nil

lazy val foreachStatement =
  ":" ~> innerStatementList <~ "endforeach" <~ ";" | 
  statement ^^ (List(_)) | 
  ";" ^^^ Nil
...
{% endhighlight %}
</div>
</div>

Most of the differences are based on the fact that many of the yacc rules are expressed recursively, which should be converted to a more explicit form when using scala combinators.

It should be pointed out though, that some parts of the parser - especially those that handle operator precedence - are vastly different from the original bison code. In bison operator precedence can be configured/defined at a global level, white scala combinators require a more explicit implementation.

# Somewhat unfair statistics

The technologies are vastly different, nevertheless one can do line counting:

## Lexical analyzer

{:class="table table-condensed table-bordered"}
|------------------------------------------------|
|       | PHP flex   | JBJ scala combinators     |
|------------------------------------------------|
| Files | 1          | 10                        | 
| Lines | 2442       | 493                       | 
|------------------------------------------------|

## Syntactic parser

{:class="table table-condensed table-bordered"}
|------------------------------------------------|
|       | PHP bison  | JBJ scala combinators     |
|------------------------------------------------|
| Files | 1          | 1                         | 
| Lines | 1283       | 847                       | 
|------------------------------------------------|
