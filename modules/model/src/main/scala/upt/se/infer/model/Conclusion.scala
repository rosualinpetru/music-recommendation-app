package upt.se.infer.model

import io.circe.generic.JsonCodec

@JsonCodec
final case class Conclusion(values: List[String])
