---
layout: page
title: "Very first converted PHP"
description: ""
breadcrumb: [index.md, talk/converter.md]
---

{% highlight php %}
This is before
<?php
    print "Hello" . " " . "world";
?>
This is after
{% endhighlight %}

{% highlight scala %}
package testunits

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.JbjCodeUnit

object hello_world extends JbjCodeUnit {

  def exec(implicit ctx: Context) {
  
    ctx.out.print("""This is before
       |""".stripMargin)
    ctx.out.print("")
    ctx.out.print(((StringVal("""Hello""") !! StringVal(""" """)) !! StringVal("""world""")).toOutput)
    ctx.out.print("""This is after
       |""".stripMargin)
  }
}
{% endhighlight %}

This happened at March 7 2014 on a train ride from Berlin to Dortmund.

Comment from a colleague: "After gaining consciousness its first intend was to kill its creator."
