package upt.se.infer.model

import io.circe.generic.JsonCodec
import upt.se.infer._
import upt.se.infer.model.{FixedArg => FA, Rule => R, VariableArg => VA}

@JsonCodec
final case class Rule(implication: Predicate, conditions: List[Predicate]) {
  override def toString: String =
    s"$implication <- ${conditions.mkString(" ^ ")}"
}

object Rule {
  private val intensityRules = List(
    "has_intensity" ~> FA("medium") <--- "mood" ~> FA("happy"),
    "has_intensity" ~> FA("high") <--- "mood" ~> FA("exuberant"),
    "has_intensity" ~> FA("very_high") <--- "mood" ~> FA("energetic"),
    "has_intensity" ~> FA("high") <--- "mood" ~> FA("frantic"),
    "has_intensity" ~> FA("medium") <--- "mood" ~> FA("sad"),
    "has_intensity" ~> FA("low") <--- "mood" ~> FA("depression"),
    "has_intensity" ~> FA("very_low") <--- "mood" ~> FA("calm"),
    "has_intensity" ~> FA("low") <--- "mood" ~> FA("contentment")
  )

  private val timbreRules = List(
    "has_timbre" ~> FA("medium") <--- "mood" ~> FA("happy"),
    "has_timbre" ~> FA("medium") <--- "mood" ~> FA("exuberant"),
    "has_timbre" ~> FA("medium") <--- "mood" ~> FA("energetic"),
    "has_timbre" ~> FA("very_high") <--- "mood" ~> FA("frantic"),
    "has_timbre" ~> FA("low") <--- "mood" ~> FA("sad"),
    "has_timbre" ~> FA("very_low") <--- "mood" ~> FA("depression"),
    "has_timbre" ~> FA("low") <--- "mood" ~> FA("calm"),
    "has_timbre" ~> FA("very_low") <--- "mood" ~> FA("contentment")
  )

  private val pitchRules = List(
    "has_pitch" ~> FA("very_high") <--- "mood" ~> FA("happy"),
    "has_pitch" ~> FA("high") <--- "mood" ~> FA("exuberant"),
    "has_pitch" ~> FA("medium") <--- "mood" ~> FA("energetic"),
    "has_pitch" ~> FA("low") <--- "mood" ~> FA("frantic"),
    "has_pitch" ~> FA("very_low") <--- "mood" ~> FA("sad"),
    "has_pitch" ~> FA("low") <--- "mood" ~> FA("depression"),
    "has_pitch" ~> FA("medium") <--- "mood" ~> FA("calm"),
    "has_pitch" ~> FA("high") <--- "mood" ~> FA("contentment")
  )

  private val rhythmRules = List(
    "has_rhythm" ~> FA("very_high") <--- "mood" ~> FA("happy"),
    "has_rhythm" ~> FA("high") <--- "mood" ~> FA("exuberant"),
    "has_rhythm" ~> FA("high") <--- "mood" ~> FA("energetic"),
    "has_rhythm" ~> FA("very_high") <--- "mood" ~> FA("frantic"),
    "has_rhythm" ~> FA("low") <--- "mood" ~> FA("sad"),
    "has_rhythm" ~> FA("low") <--- "mood" ~> FA("depression"),
    "has_rhythm" ~> FA("very_low") <--- "mood" ~> FA("calm"),
    "has_rhythm" ~> FA("low") <--- "mood" ~> FA("contentment")
  )

  private val genreRule = "has_genre" ~> VA("Genre") <---
    (
      "liked_artist" ~> VA("Name"),
      "artist" ~> (VA("Name"), DK, VA("Genre"))
    )

  private val sameCountryRule = "artist_country" ~> VA("Country") <---
    (
      "living_in" ~> VA("Country")
    )

  private val likedArtistCountryRule = "artist_country" ~> VA("Country") <---
    (
      "liked_artist" ~> VA("Name"),
      "artist" ~> (VA("Name"), VA("Country"), DK),
    )

  private val songRule =
    "suggested_song" ~> (VA("SongName"), VA("ArtistName")) <---
      (
        "artist_country" ~> VA("ArtistCountry"),
        "has_intensity" ~> VA("I"),
        "has_timbre" ~> VA("T"),
        "has_pitch" ~> VA("P"),
        "has_rhythm" ~> VA("R"),
        "has_genre" ~> VA("Genre"),
        "song" ~> (
          VA("SongName"),
          "artist" ~> (VA("ArtistName"), VA("ArtistCountry"), DK),
          "intensity" ~> VA("I"),
          "timbre" ~> VA("T"),
          "pitch" ~> VA("P"),
          "rhythm" ~> VA("R"),
          "genre" ~> VA("Genre")
        )
      )

  def rules(): List[R] = {
    intensityRules ++ pitchRules ++ timbreRules ++ rhythmRules ++ List(
      genreRule,
      sameCountryRule,
      likedArtistCountryRule,
      songRule
    )
  }
}
