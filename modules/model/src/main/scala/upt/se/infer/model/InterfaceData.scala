package upt.se.infer.model

import io.circe.generic.JsonCodec

@JsonCodec
case class InterfaceData(systemName: String, systemDescription: String, inputFields: List[InputField])
