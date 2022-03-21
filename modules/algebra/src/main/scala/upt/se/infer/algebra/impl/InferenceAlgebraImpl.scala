package upt.se.infer.algebra.impl

import fs2.io.file.Files
import io.circe.syntax.EncoderOps
import upt.se.infer.StringOps
import upt.se.infer.algebra.InferenceAlgebra
import upt.se.infer.model.{
  Argument,
  FixedArg,
  Goal,
  Predicate,
  Rule,
  UserInput,
  VariableArg
}

import scala.annotation.tailrec

private[algebra] final class InferenceAlgebraImpl[F[_]: Async]
    extends InferenceAlgebra[F] {

  def inferSolution(userInput: UserInput): F[List[Goal]] = {
    for {
      // Read rules from the knowledge base
      rules <- readRules()
      // Read goal name from the database
      goalName <- readGoalPredicateName()
      // Set initial premises
      database <- readDatabase()
      premises = userInput.premises ++ database
      // Apply fixed point algorithm and generate all possible premises using the inference rules
      newPremises = fixedPointRuleGeneration(premises, rules)
      // Extract all premises that represent a goal
      goals = newPremises.filter(_.name == goalName)
      result = goals.map(p => Goal(p.name, p.args.map(_.toString)))
      _ <- fs2.Stream
        .emits(rules)
        .map(_.asJson.toString())
        .through(fs2.text.utf8.encode)
        .through(Files[F].writeAll(fs2.io.file.Path("rules.txt")))
        .compile
        .drain
      _ <- fs2.Stream
        .emits(database)
        .map(_.asJson.toString())
        .through(fs2.text.utf8.encode)
        .through(Files[F].writeAll(fs2.io.file.Path("database.txt")))
        .compile
        .drain
    } yield result
  }

  private def readRules(): F[List[Rule]] = Rule.rules().pure[F]

  private def readDatabase(): F[List[Predicate]] = Predicate.database().pure[F]

  private def readGoalPredicateName(): F[String] = "suggested_song".pure[F]

  // a rule is satisfiable if all conditions can be found among the premises
  // additionally, all conditions are not allowed to have variables
  private def isSatisfiable(rule: Rule, premises: List[Predicate]): Boolean =
    rule.conditions.forall(c =>
      !hasVariables(c) && premises.exists(prem =>
        arePredicatesUnifiable(prem, c)
      )
    )

  private def generateNewRules(
      rule: Rule,
      premises: List[Predicate]
  ): List[Rule] = {

    def isConditionSatisfiable(condition: Predicate): Boolean =
      hasVariables(condition) || premises.exists(p =>
        arePredicatesUnifiable(p, condition)
      )

    def filterRules(rules: List[Rule]) = rules
      .filter(r => r.conditions.forall(isConditionSatisfiable))

    @tailrec
    def internal(rules: List[Rule]): List[Rule] = {
      // Recursive algorithm that stops when all new rules no longer contain variables in their conditions
      if (!rules.exists(_.conditions.exists(hasVariables))) {
        rules
      } else {
        val newRules = for {
          // For every rule
          rule <- rules
          // and for each condition of every rule
          condition <- rule.conditions if hasVariables(condition)
          // find all possible unification in the existing premises
          possibleUnification <- premises.filter(prem =>
            arePredicatesUnifiable(prem, condition)
          )
          // and every possible substitution determined by the unification
          substitution <- substitution(possibleUnification, condition)
        } yield {
          val newImpl =
            unify(rule.implication, substitution._1, substitution._2)
          val newConditions =
            rule.conditions.map(c => unify(c, substitution._1, substitution._2))
          Rule(newImpl, newConditions)
        }
        // as substitutions were made, some rules might not be satisfiable so we filter them out
        val filteredRules = filterRules(newRules.distinct)
        internal(filteredRules)
      }
    }

    internal(List(rule))

  }

  // Generates rules and validate existing ones, generating new premises, until no more rules are generated.
  @tailrec
  private def fixedPointRuleGeneration(
      premises: List[Predicate],
      rules: List[Rule]
  ): List[Predicate] =
    rules match {
      case rule :: next =>
        val (newPremises, newRules) = {
          // a rule can either still have a variable that needs unification or there are no variables
          // `don't care` is not considered a variable
          if (rule.conditions.exists(hasVariables)) {
            // if there are variables, try to generate new rules based on existing premises
            // by unification
            val newRules = generateNewRules(rule, premises) ++ next
            (premises, newRules.distinct)
          } else {
            // if there are no more variables in the inference rule,
            // validate all conditions based on existing premises and
            // if all conditions are met, generate a new premise
            val newPremises =
              if (isSatisfiable(rule, premises)) rule.implication :: premises
              else premises

            (newPremises.distinct, next)
          }
        }

        // Redo until no more rules are generated
        fixedPointRuleGeneration(
          newPremises,
          newRules
        )

      case Nil => premises
    }

  private def arePredicatesUnifiable(
      pred1: Predicate,
      pred2: Predicate
  ): Boolean =
    pred1.name == pred2.name && pred1.args.zip(pred2.args).forall {
      case (FixedArg(v1), FixedArg(v2)) => v1 == v2
      case (p1: Predicate, p2: Predicate) =>
        arePredicatesUnifiable(p1, p2)
      case _ => true
    }

  private def unify(
      pred: Predicate,
      arg1: VariableArg,
      arg2: Argument
  ): Predicate =
    pred.name ~>
      (pred.args.map {
        case v: VariableArg => if (v.symbol == arg1.symbol) arg2 else v
        case p: Predicate   => unify(p, arg1, arg2)
        case arg            => arg
      }: _*)

  private def substitution(
      p1: Predicate,
      p2: Predicate
  ): List[(VariableArg, Argument)] = {
    if (p1.name != p2.name || p1.args.size != p2.args.size)
      List()
    else
      p1.args.zip(p2.args).foldLeft(List[(VariableArg, Argument)]()) {
        case (acc, (arg1: VariableArg, arg2: Argument)) => (arg1, arg2) :: acc
        case (acc, (arg1: Argument, arg2: VariableArg)) => (arg2, arg1) :: acc
        case (acc, (arg1: Predicate, arg2: Predicate)) =>
          substitution(arg1, arg2) ++ acc
        case (acc, _) => acc
      }
  }

  private def hasVariables(predicate: Predicate): Boolean =
    predicate.args.exists {
      case _: VariableArg => true
      case p: Predicate   => hasVariables(p)
      case _              => false
    }

}
