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
