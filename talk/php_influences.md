---
layout: page
title: "The many influences of PHP"
description: ""
breadcrumb: [index.md, talk/breakdown.md]
root: ..
---

# The mean features

Over the years PHP was influenced by many other languages and collected various "features" that make a simple conversion to Java/Scala quite challenging.
In the following we will discuss some of the major problems in detail.

## Basic types and implicit conversions

The PHP language has the following primitive types

* Boolean
* Integer
  * Actual precision depends on system
  * Usually 64-bit on modern systems
* Floating point
  * Actual precision depends on system
  * Usually double precision on modern systems
* String
  * For backward compatibility this is actually a byte string
  * PHP 6 will potentially introduce native support for unicode
* Array
  * This is actually a combination between indexed lists and string-keyed hash maps
* Object
  * I.e. a reference to an instance of a class
* NULL
* Resource
  * Encapsulates references to system resources like file descriptors etc.
* Callable
  * This can be a string, array or object which is interpreted as reference to a function or method

Apart from some specialties one might think that these are not so far away from the basic types of Java/Scala. Though the main problem one quickly encounters are the implicit type conversion that are quite similar to Perl.

* String concatenation (dot operator like in Perl) implicitly assumes that both operants are strings

  ~~~
  "Hello " . "42"
                     ---> (string) "Hello 42"
  "Hello " . 42
                     ---> (string) "Hello 42"
  ~~~

* Arithmetic operators implicitly assume that both operants are numbers (integer of floating point)

  ~~~
  "Hello " + "42"
                     ---> (int) 42
  " 1e5 " + 42
                     ---> (double) 100042
  ~~~

* Logical operators implicitly assume that both operants are booleans

  ~~~
  "Hello " && true
                     ---> (boolean) true
  "false" && true
                     ---> (boolean) true
  "" && true
                     ---> (boolean) false
  0 && true
                     ---> (boolean) false
  ~~~

* Bitwise operators behave differently for numbers and string

  ~~~
  "Hello" | "abcde"
                     ---> (string) "igolo"
  "Hello" | 10
                     ---> (int) 10
  "13" | 10
                     ---> (int) 15
  ~~~

* Comparison operators depend on the given types, in case of strings even on the content

  ~~~
  "42" < "10000"
                     ---> (boolean) true
  "42a" < "10000"
                     ---> (boolean) false
  42 < "10000"
                     ---> (boolean) true
  42 < "10000a"
                     ---> (boolean) true
  42 < "a10000"
                     ---> (boolean) false
  ~~~

* pre/post-increment/decrement behave differently for numbers and strings

  ~~~
  $a = 1
  $a++
                    ---> (int) 2
  $a = 10
  $a--
                    ---> (int) 9
  $a = "Hello"
  $a++
                    ---> (string) "Hellp"
  $a = "Hello"
  $a--
                    ---> (string) "Hello"
  ~~~

## By-Reference

### Function parameters

PHP has a C++ like syntax for passing parameters by reference

{% highlight php linenos %}
<?php

function squareIt(&$x) {
    $x = $x * $x;
}

$a = 2;
squareIt($a);
print "Result: $a\n";
?>
{% endhighlight %}

~~~
Result: 4
~~~

### Variables

{% highlight php linenos %}
<?php
$a = 4;
$b = array(1, 2, 3, &$a);
$c = &$a;

echo "1. b[3] = ${b[3]}  a = $a\n";
$c = 1;
echo "2. b[3] = ${b[3]}  a = $a\n";
$b[3] = 8;
echo "3. c = $c   a = $a\n";
?>
{% endhighlight %}

~~~
1. b[3] = 4  a = 4
2. b[3] = 1  a = 1
3. c = 8   a = 8
~~~

## Classes and interfaces (and traits)

PHP has a Java like syntax to define classes and interfaces. With PHP 5.4 this has been extended by traits as well.

But: Unlike Java PHP classes support a destructor, even though there is no "delete" like in C++. Instead the destructor is called immediately once the last reference to an instance is unset.

{% highlight php linenos %}
<?php

class A {
    function __construct() {
        print "constructor\n";
    }

    function __destruct() {
        print "destructor\n";
    }
}

print "start\n";
$a = new A();
print "middle\n";
$a = NULL;
print "end\n";
?>
{% endhighlight %}

~~~
start
constructor
middle
destructor
end
~~~

## Implicit array and class creation

{% highlight php linenos %}
<?php

$a[][][] = 3;

var_dump($a);

$b[1][2]->bla = "Hello";

var_dump($b);
?>
{% endhighlight %}

~~~
array(1) {
  [0] =>
  array(1) {
    [0] =>
    array(1) {
      [0] =>
      int(3)
    }
  }
}
PHP Strict standards:  Creating default object from empty value in t.php on line 7

array(1) {
  [1] =>
  array(1) {
    [2] =>
    class stdClass#1 (1) {
      public $bla =>
      string(5) "Hello"
    }
  }
}
~~~

## Namespaces

PHP has a C++ like syntax to define namespaces

{% highlight php linenos %}
<?php
namespace N1 {
    function a() {
        print "In a\n";
    }
    \N2\b();
}

namespace N2 {
    function b() {
        print "In b\n";
    }
    \N1\a();
}
?>
{% endhighlight %}

~~~
In b
In a
~~~

## Lambda expressions / closures

As of PHP 5.3 there is a javascript like support for anonymous functions aka closures.

{% highlight php linenos %}
<?php
$func = function($x) {
    print "$x\n";
};
$func("Hello");
?>
{% endhighlight %}

~~~
Hello
~~~

## Generators

As of PHP 5.5 there is a python like support for generators.

{% highlight php linenos %}
<?php
function generateNums() {
    for ( $i = 1; $i < 5; $i++ ) {
        yield $i;
    }
};
$generator = generateNums(); // this is a Generator class implementing the Iterator interface
foreach ($generator as $value) {
    print "Value: $value\n";
}
?>
{% endhighlight %}

~~~
Value: 1
Value: 2
Value: 3
Value: 4
~~~

## Inline HTML

PHP was originally designed to be embedded inside regular HTML.

{% highlight php linenos %}
<html>
<head></head>
<body>
<?php
  $values = array(1,2,3,4);
?>
<ul>
<?php foreach($values as $value) { ?>
  <li><?php echo $value ?></li>
<?php } ?>
</ul>
</body>
</html>
{% endhighlight %}

~~~ html
<html>
<head></head>
<body>
<ul>
  <li>1</li>
  <li>2</li>
  <li>3</li>
  <li>4</li>
</ul>
</body>
</html>
~~~

# A short view at the PHP interpreter and its Zend engine

The modern PHP interpreter is now based on the Zend engine. At its core it consists of a parser/compiler that converts the source code to a series of opcodes that are interpreted by the Zend engine.

Example:

{% highlight php linenos %}
<?php

$obj = new A();
?>
{% endhighlight %}

will be compiled to

{:class="table table-condensed table-bordered"}
|-----------------------------------------------------------|
| line | op               | fetch | ext | return | operants |
|-----------------------------------------------------------|
| 3    | ZEND_FETCH_CLASS |       |     | :0     | 'A'      |
|      | NEW              |       |     | $1     | :0       |
|      | DO_FCALL_BY_NAME |       | 0   |        |          |
|      | ASSIGN           |       |     |        | !0,$1    |
| 4    | RETURN           |       |     |        | 1        |
|-----------------------------------------------------------|

In other words: The PHP interpreter operates more like a VM with integrated compiler instead of a classical syntax tree based interpreter.

If you just want to create a compatible PHP interpreter with good performance it might be feasible to recreate the Zend engine in Java and recode the parser/compiler with almost no modifications at all.
For recoding this approach is not recommended though, since too much information of the original source code is lost in the process. I.e. you might be able to generate code that "just works", but will not be readable at all.