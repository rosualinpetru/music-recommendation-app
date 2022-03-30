package upt.se.infer.model

import upt.se.infer._
import upt.se.infer.model.{FixedArg => FA, Rule => R, VariableArg => VA}

final case class Rule(implication: Predicate, conditions: List[Predicate]) {
  override def toString: String =
    s"$implication <- ${conditions.mkString(" & ")}"
}

object Rule {

  def fromString(s: String): Rule = {
    val (implicationS, conditionsS) = {
      val sep = s.split(" <- ")
      (sep(0), sep(1).split(" & ").toList)
    }

    Rule(
      Predicate.fromString(implicationS),
      conditionsS.map(Predicate.fromString)
    )
  }
}
