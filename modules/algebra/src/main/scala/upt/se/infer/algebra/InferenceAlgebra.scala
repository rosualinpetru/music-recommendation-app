package upt.se.infer.algebra

import upt.se.infer.algebra.impl.InferenceAlgebraImpl
import upt.se.infer.model.{Conclusion, InterfaceData, UserInput}

trait InferenceAlgebra[F[_]] {
  def getInterfaceData(): F[InterfaceData]
  def inferSolution(userInput: UserInput): F[List[Conclusion]]

}

object InferenceAlgebra {
  def resource[F[_]: Async]: Resource[F, InferenceAlgebra[F]] = {
    new InferenceAlgebraImpl().pure[Resource[F, *]].widen
  }
}
