package upt.se.infer.model

import io.circe.generic.JsonCodec
import upt.se.infer.StringOps

@JsonCodec
final case class UserInput(inputs: Map[String, String]) {

  def asPredicates: List[Predicate] =
    inputs.map { case (input, value) => input ~> FixedArg(value) }.toList

}
