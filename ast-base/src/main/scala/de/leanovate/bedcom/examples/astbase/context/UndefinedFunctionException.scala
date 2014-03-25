package de.leanovate.bedcom.examples.astbase.context

class UndefinedFunctionException(name:String) extends RuntimeException(s"Function $name not defined") {

}
