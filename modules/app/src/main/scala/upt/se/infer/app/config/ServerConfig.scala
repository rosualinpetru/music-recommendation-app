package upt.se.infer.app.config

import ciris._
import com.comcast.ip4s._

private [app] final case class ServerConfig private (host: Host, port: Port, threads: Int)

object ServerConfig {
  def load[F[_]: Async]: Resource[F, ServerConfig] = {
    val config = (
      default(host"0.0.0.0"),
      default(port"31513"),
      default(8)
      ).parMapN(ServerConfig.apply)

    Resource.eval(config.load[F])
  }
}
