---
layout: page
title: "Recent converter examples"
description: ""
breadcrumb: [index.md, talk/converter.md]
---

{% highlight php linenos %}
This is before
<?php
    print "Hello" . " " . "world";
?>
This is after
{% endhighlight %}

{% highlight scala linenos %}
trait hello_world extends JbjCodeUnit {

  def exec(implicit ctx: Context) {
    
    inline("This is before\n")
    print(p("Hello") !! p(" ") !! p("world"))
    inline("This is after\n")
  }
}
{% endhighlight %}


{% highlight php linenos %}
<?php
$a = "Hello";
$b = "world";
$c = $a . " " . $b;

echo $c;

$d = $c + 42;

echo $d;
?>
{% endhighlight %}

{% highlight scala linenos %}
trait hello_world2 extends JbjCodeUnit {

  def exec(implicit ctx: Context) {
    val a = lvar("a")
    val b = lvar("b")
    val c = lvar("c")
    val d = lvar("d")
    
    a := p("Hello")
    b := p("world")
    c := a !! p(" ") !! b
    echo(c)
    d := c + p(42L)
    echo(d)
  }
}
{% endhighlight %}

{% highlight php linenos %}
<?php
    $a = array("Hello", "World", 42);

    for($i = 0; $i < count($a); $i++) {
        echo $a[$i];
        $a[$i] = ($i + 2) * $i + 1;
        echo "\n";
    }

    for($i = 0; $i < count($a); $i++) {
        echo $a[$i];
        echo "\n";
    }
?>
{% endhighlight %}

{% highlight scala linenos %}
trait hello_world3 extends JbjCodeUnit {

  def exec(implicit ctx: Context) {
    val a = lvar("a")
    val i = lvar("i")
    
    a := array(p("Hello"), p("World"), p(42L))
    pFor(i := p(0L), i < p(count(a)), i.++) {
      echo(a.dim(i))
      a.dim(i) := (i + p(2L)) * i + p(1L)
      echo(p("\n"))
    }
    pFor(i := p(0L), i < p(count(a)), i.++) {
      echo(a.dim(i))
      echo(p("\n"))
    }
  }
}
{% endhighlight %}

{% highlight php linenos %}
<?php

function specialSum($a, $b) {
    if ($a < 10) {
        return 2 * $a + $b;
    } else {
        return $a + $b;
    }
}

echo specialSum(2, 3);
echo "\n";
echo specialSum(6, 7);
echo "\n";
echo specialSum(12, 13);
echo "\n";
?>
{% endhighlight %}

{% highlight scala linenos %}
trait hello_world4 extends JbjCodeUnit {

  def specialSum(a: PVal, b: PVal)(implicit callerCtx: Context): PAny =
    functionCtx("specialSum",callerCtx) { ctx:Context =>
      _specialSum(lvar("a", a)(ctx), lvar("b", b)(ctx))(ctx)
    }
    
  def _specialSum(a: Reference, b: Reference)(implicit ctx: Context): PAny = {
    
    if(a < p(10L)) {
       p(2L) * a + b
    } else { 
       a + b
    }
  }
  
  def exec(implicit ctx: Context) {
    
    echo(p(specialSum(p(2L), p(3L))))
    echo(p("\n"))
    echo(p(specialSum(p(6L), p(7L))))
    echo(p("\n"))
    echo(p(specialSum(p(12L), p(13L))))
    echo(p("\n"))
  }
}
{% endhighlight %}
