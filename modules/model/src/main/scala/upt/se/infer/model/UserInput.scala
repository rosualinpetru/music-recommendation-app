package upt.se.infer.model

import io.circe.generic.JsonCodec
import upt.se.infer.StringOps

@JsonCodec
final case class UserInput(mood: String, country: String, likedArtist: String) {

  def premises =
    List(
      "mood" ~> FixedArg(mood),
      "living_in" ~> FixedArg(country),
      Predicate("liked_artist", List(FixedArg(likedArtist)))
    )

}
