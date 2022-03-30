package upt.se.infer.model

import upt.se.infer._
import upt.se.infer.model.{FixedArg => FA}

sealed trait Argument

object Argument {
  def fromString(s: String): Argument = s match {
    case "_" => DK
    case v if v.startsWith("$") => VariableArg(v)
    case f if !f.contains("(") => FixedArg(f)
    case p => Predicate.fromString(p)
  }
}

final case class VariableArg(symbol: String) extends Argument {
  override def toString: String = symbol.toUpperCase
}

final case class FixedArg(symbol: String) extends Argument {
  override def toString: String = symbol
}

case object DK extends Argument {
  override def equals(obj: Any): Boolean = true
  override def toString: String = "_"
}

final case class Predicate(name: String, args: List[Argument]) extends Argument {
  override def toString: String = name + "(" + args.mkString(", ") + ")"
}

object Predicate {

  def fromString(s: String): Predicate = {

    val name = s.takeWhile(_ != '(')
    val (t, h) = s.stripPrefix(s"$name(").stripSuffix(")").replace(", ", ",")
      .foldLeft((List[String](), "")) { case ((acc, aux), c) =>
        c match {
          case ',' if aux.count(_ == '(') == aux.count(_ == ')') => (aux::acc, "")
          case _ => (acc, aux+c)
        }
      }

    val args = (h::t).reverse

    Predicate(name, args.map(Argument.fromString))
  }
}
