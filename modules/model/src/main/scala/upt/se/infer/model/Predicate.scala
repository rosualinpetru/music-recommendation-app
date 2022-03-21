package upt.se.infer.model

import upt.se.infer._
import upt.se.infer.model.{FixedArg => FA}
import io.circe.generic.JsonCodec

@JsonCodec
sealed trait Argument

@JsonCodec
final case class VariableArg(symbol: String) extends Argument {
  override def toString: String = symbol.toUpperCase
}

@JsonCodec
final case class FixedArg(symbol: String) extends Argument {
  override def toString: String = symbol
}

case object DK extends Argument {
  override def equals(obj: Any): Boolean = true
  override def toString: String = "_"
}

@JsonCodec
final case class Predicate(name: String, args: List[Argument]) extends Argument {
  override def toString: String = name + "(" + args.mkString(", ") + ")"
}

object Predicate {
  def database(): List[Predicate] = List(
    "artist" ~> (FA("Maluma"), FA("Columbia"), FA("latino")),
    "artist" ~> (FA("Camilo"), FA("Columbia"), FA("latino")),
    "artist" ~> (FA("Ricky Martin"), FA("Spain"), FA("latino")),
    "artist" ~> (FA("Alexandra"), FA("Serbia"), FA("serbian")),
    //Song
    "song" ~> (FA("Desconocidos"),
    "artist" ~> (FA("Camilo"), FA("Columbia"), FA("latino")),
    "intensity" ~> FA("medium"),
    "timbre" ~> FA("medium"),
    "pitch" ~> FA("very_high"),
    "rhythm" ~> FA("very_high"),
    "genre" ~> FA("latino")),
    //Song
    "song" ~> (FA("11 AM"),
    "artist" ~> (FA("Maluma"), FA("Columbia"), FA("latino")),
    "intensity" ~> FA("medium"),
    "timbre" ~> FA("medium"),
    "pitch" ~> FA("very_high"),
    "rhythm" ~> FA("very_high"),
    "genre" ~> FA("latino")),
    //Song
    "song" ~> (FA("Ljubav ili ludilo"),
    "artist" ~> (FA("Alexandra"), FA("Serbia"), FA("serbian")),
    "intensity" ~> FA("medium"),
    "timbre" ~> FA("medium"),
    "pitch" ~> FA("very_high"),
    "rhythm" ~> FA("very_high"),
    "genre" ~> FA("serbian"))
  )
}
