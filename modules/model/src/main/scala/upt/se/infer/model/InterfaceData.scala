package upt.se.infer.model

import io.circe.generic.JsonCodec

@JsonCodec
case class InterfaceData(systemName: String, inputFields: List[InputField])
