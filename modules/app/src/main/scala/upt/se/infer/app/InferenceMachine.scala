package upt.se.infer.app

import upt.se.infer.app.config.ServerConfig

object InferenceMachine extends ResourceApp.Forever {

  override def run(args: List[String]): Resource[IO, Unit] = resource[IO]

  private def resource[F[_]: Async]: Resource[F, Unit] = for {
    serverConfig <- ServerConfig.load
    server <- InferenceServer.resource(serverConfig)
     _ <- server.start
  } yield ()

}
