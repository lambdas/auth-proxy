package lambdas.ea

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory

import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.io.StdIn
import scala.jdk.CollectionConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {
  private val config = ConfigFactory.load()
  private val destinationScheme = config.getString("server.destination-scheme")
  private val destinationHost = config.getString("server.destination-host")
  private val destinationPort = config.getInt("server.destination-port")
  private implicit val system = ActorSystem(Behaviors.empty, "endpoint-auth")
  private val pendingRequests = new ConcurrentHashMap[String, Promise[Unit]]

  def main(args: Array[String]): Unit = {
    implicit val executionContext = system.executionContext
    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(routes)

    Await.ready(Future.never, Duration.Inf)

    println(s"Server is now online.\nPress RETURN to stop...")

    // todo: figure this crap out
    // StdIn.readLine()
    // bindingFuture
    //   .flatMap(_.unbind())
    //   .onComplete(_ => system.terminate())
  }

  private def routes: Route = {
    concat(
      (post & path("authorize")) { authorize },
      get                        { resource  },
    )
  }

  private def resource: Route = {
    headerValueByName("Auth-Key") { authKey =>
      extractExecutionContext { implicit ec =>
        extractRequest { request =>
          val promise = Promise[Unit]
          pendingRequests.put(authKey, promise)
          complete(promise.future.flatMap(_ => forwardedResource(request.uri))) 
        }
      }
    }
  }

  private def authorize: Route = {
    headerValueByName("Auth-Key") { authKey =>
      Option(pendingRequests.remove(authKey)).foreach(_.success(()))
      complete(StatusCodes.OK)
    }
  }

  private def forwardedResource(uri: Uri): Future[HttpResponse] = {
    val destinationUri = 
      uri
        .withHost(destinationHost)
        .withPort(destinationPort)
        .withScheme(destinationScheme)

    Http().singleRequest(HttpRequest(uri = destinationUri))
  }
}
