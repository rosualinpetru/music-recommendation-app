package upt.se.infer.routes

import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import upt.se.infer.algebra.InferenceAlgebra
import upt.se.infer.model.UserInput

final class InferenceRoutes[F[_]: Async](inferenceAlgebra: InferenceAlgebra[F])
    extends Http4sDsl[F] {

  implicit val decoderInput: EntityDecoder[F, UserInput] = jsonOf[F, UserInput]

  def routes: HttpRoutes[F] = HttpRoutes.of {
    case _ @ GET -> Root / "interface" =>
      for {
        data <- inferenceAlgebra.getInterfaceData()
        response <- Ok(data.asJson)
      } yield response

    case req @ POST -> Root / "input" =>
      for {
        userInput <- req.as[UserInput]
        solution <- inferenceAlgebra.inferSolution(userInput)
        response <- Ok(solution.asJson)
      } yield response
  }
}

object InferenceRoutes {
  def resource[F[_]: Async](
      inferenceAlgebra: InferenceAlgebra[F]
  ): Resource[F, InferenceRoutes[F]] =
    new InferenceRoutes(inferenceAlgebra).pure[Resource[F, *]].widen
}
