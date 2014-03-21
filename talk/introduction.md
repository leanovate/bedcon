---
layout: page
title: "Introduction"
description: ""
breadcrumb: [index.md]
---

# Name of the game

As we try to bring PHP to the Java world the name is based on a simple regression:

~~~
PHP -> OGO -> NFN -> MEM -> LDL -> KCK -> JBJ
~~~

By shear coincidence this leads to a three letters that may be interpreted as some funny acronyms, including - but not restricted to - the name of the author himself.

# Positioning of the project

JBJ is not the first project trying to bring PHP to the Java-VM. Most prominently there are:

* [Quercus](http://quercus.caucho.com/)
  * Almost complete implementation of the PHP interpreter in Java
  * Part of the Caucho/Resin. Sourcecode available, GPL license
  * Does not seem to be community driven
* [IBM Project Zero/WebSphere sMash](http://en.wikipedia.org/wiki/Project_zero)
  * Almost no information available (any more?)
  * Potentially dead project(?)

Both of these projects focus on interpretation of PHP code. In contrast JBJ tries to create an environment that should support a semi-automatic conversion of PHP code to Scala code.
In theory a conversion to plain Java should be possible as well, but will most likely create rather mess/unreadable code (mostly due to the lack of operator-overloading).

For completeness, it should be mentioned that there is an inverse project as well:

* [PJP - PHP/Java Bridge](http://php-java-bridge.sourceforge.net/pjb/)
  * Tries to integrate a Java-VM into the PHP interpreter
  * Is able to call Java-classes from PHP code
