---
layout: page
title: "Testing the interpreter"
description: ""
breadcrumb: [index.md, talk/abstract_syntax_tree.md]
root: ..
---

# Use the existing testsuite of the PHP interpreter itself

The unit tests of the PHP interpreter consists of a log of ".phpt" files which contain a script to be run and it expected output:

{% highlight php linenos %}
--TEST--
Testing recursive function
--FILE--
<?php

function Test()
{
        static $a=1;
        echo "$a ";
        $a++;
        if($a<10): Test(); endif;
}

Test();

?>
--EXPECT--
1 2 3 4 5 6 7 8 9
{% endhighlight %}

{% highlight scala linenos %}
"Testing recursive function" in {
  // lang/008
  script(
    """<?php
      |
      |function Test()
      |{
      | static $a=1;
      | echo "$a ";
      | $a++;
      | if($a<10): Test(); endif;
      |}
      |
      |Test();
      |
      |?>""".stripMargin
  ).result must haveOutput(
    """1 2 3 4 5 6 7 8 9 """.stripMargin
  )
}
{% endhighlight %}