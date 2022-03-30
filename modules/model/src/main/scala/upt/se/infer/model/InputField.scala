package upt.se.infer.model

import io.circe.generic.JsonCodec

@JsonCodec
case class InputField(name: String, question: String, values: List[String])
