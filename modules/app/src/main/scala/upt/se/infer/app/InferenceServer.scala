package upt.se.infer.app

import cats.data.NonEmptyList
import org.http4s.{Header, HttpApp, HttpRoutes, headers}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import org.http4s.server.middleware.CORS
import org.typelevel.ci.CIString
import upt.se.infer.algebra.InferenceAlgebra
import upt.se.infer.app.config.ServerConfig
import upt.se.infer.routes.InferenceRoutes

private final class InferenceServer private (config: ServerConfig) {

  def bindServer[F[_]: Async](httpApp: HttpApp[F]):  Resource[F, Server] = BlazeServerBuilder[F]
    .withConnectorPoolSize(config.threads)
    .bindHttp(
      config.port.value,
      config.host.toString
    )
    .withoutBanner
    .withHttpApp(httpApp)
    .resource

  def buildHttpApp[F[_]: Async](routes: InferenceRoutes[F]): HttpApp[F] = {
    val apiRoutes = NonEmptyList.of(routes.routes).reduceK

    val cors = List[Header.ToRaw](
      Header.Raw(CIString("Access-Control-Allow-Origin"), "*"),
      Header.Raw(CIString("Access-Control-Allow-Methods"), "GET, POST, PATCH, PUT, DELETE, OPTIONS"),
      headers.`Access-Control-Allow-Headers`(
        List(
          CIString("Origin"),
          CIString("Content-Type"),
          CIString("X-Authentication-Token"),
        )
      ),
    )

    val withCORS: HttpRoutes[F] = CORS.policy
      .withAllowCredentials(false)
      .withAllowOriginAll
      .httpRoutes(apiRoutes.map(_.putHeaders(cors: _*)))

    Router("/api" -> withCORS).orNotFound
  }

  def start[F[_]: Async]: Resource[F, Server]  = for {

    algebra <- InferenceAlgebra.resource
    routes <- InferenceRoutes.resource(algebra)
   httpApp = buildHttpApp(routes)
    server <- bindServer(httpApp)
  } yield server


}

private object InferenceServer {
    def resource[F[_]: Async](serverConfig:  ServerConfig): Resource[F, InferenceServer] =
      new InferenceServer(serverConfig).pure[Resource[F, *]].widen
}
