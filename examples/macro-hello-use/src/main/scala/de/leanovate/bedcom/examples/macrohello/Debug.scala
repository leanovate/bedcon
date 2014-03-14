package de.leanovate.bedcom.examples.macrohello

object Debug extends App {
  var a = 10

  DebugMacro.debug(a)

  DebugMacro.debug(2 * a)
}
