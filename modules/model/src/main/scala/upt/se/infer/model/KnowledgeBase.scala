package upt.se.infer.model

import io.circe._
import io.circe.syntax._

case class KnowledgeBase(
    interfaceData: InterfaceData,
    conclusionName: String,
    premises: List[Predicate],
    rules: List[Rule]
)

object KnowledgeBase {

  implicit val encoder: Encoder[KnowledgeBase] = (a: KnowledgeBase) =>
    Json.obj(
      ("interfaceData", a.interfaceData.asJson),
      ("conclusionName", a.conclusionName.asJson),
      ("premises", a.premises.map(_.toString).asJson),
      ("rules", a.rules.map(_.toString).asJson)
    )

  implicit val decodeFoo: Decoder[KnowledgeBase] = (c: HCursor) =>
    for {
      interfaceData <- c.downField("interfaceData").as[InterfaceData]
      conclusionName <- c.downField("conclusionName").as[String]
      premisesString <- c.downField("premises").as[List[String]]
      premises = premisesString.map(Predicate.fromString)
      rulesString <- c.downField("rules").as[List[String]]
      rules = rulesString.map(Rule.fromString)
    } yield {
      new KnowledgeBase(interfaceData, conclusionName, premises, rules)
    }
}
