package upt.se.infer.model

import io.circe.generic.JsonCodec

@JsonCodec
final case class Goal(name: String, values: List[String])
