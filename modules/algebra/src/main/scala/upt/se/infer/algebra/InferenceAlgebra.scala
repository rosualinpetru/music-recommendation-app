package upt.se.infer.algebra

import upt.se.infer.algebra.impl.InferenceAlgebraImpl
import upt.se.infer.model.{Goal, UserInput}

trait InferenceAlgebra[F[_]] {
  def inferSolution(userInput: UserInput): F[List[Goal]]
}

object InferenceAlgebra {
  def resource[F[_]: Async]: Resource[F, InferenceAlgebra[F]] = {
    new InferenceAlgebraImpl().pure[Resource[F, *]].widen
  }
}
