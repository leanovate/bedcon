package de.leanovate.bedcom.examples.astbase.context

class UndefinedVariableException(name:String) extends RuntimeException(s"Variable $name not defined") {

}
