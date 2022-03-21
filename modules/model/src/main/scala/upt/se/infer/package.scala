package upt.se

import upt.se.infer.model.{Argument, Predicate, Rule}

package object infer {
  implicit class StringOps(s: String) {
    def ~>(args: Argument*): Predicate = Predicate(s, args.toList)
  }

  implicit class PredicateOps(p: Predicate) {
    def <---(conds: Predicate*): Rule = Rule(p, conds.toList)
  }
}
