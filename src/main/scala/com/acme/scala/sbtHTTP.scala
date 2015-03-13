package com.acme.scala

import java.util.Date

import akka.actor.ActorSystem
import spray.http.HttpHeaders
import spray.routing.SimpleRoutingApp

import scala.util.Try

/*
* How to deploy this?
* - sbt assembly (make sure plugins.sbt contains "addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")" (assembly is need to include all libs in the jar)
* - WinSCP to some Linux server (does not work on Windows?)
* - in one ssh session: java -cp sbtHTTP-assembly-1.0.jar com.acme.scala.sbtHTTP
* - in the other ssh session: curl -H 'Content-Type:application/json' --data '{"where":{"trackId":{"$lt":19092}}, "select":{"_id":0, "trackId":1, "userId": 1}}' http://localhost:23456/tracks/get
* - in the first session you get "
* server:/home/youruser $ java -cp sbtHTTP-assembly-1.0.jar com.acme.scala.sbtHTTP
[INFO] [03/13/2015 13:09:32.114] [query-actor-system-akka.actor.default-dispatcher-4] [akka://query-actor-system/user/IO-HTTP/listener-0] Bound to localhost/127.0.0.1:23456
[Fri Mar 13 13:09:52 CET 2015] Received: queryRequest({"trackId":{"$lt":19092}},{"_id":0,"trackId":1,"userId":1},None)
[Fri Mar 13 13:10:42 CET 2015] Received: queryRequest({"trackId":{"$lt":19092}},{"_id":0,"trackId":1,"userId":1},None)
^Cserver:/home/youruser $"
* - in the other session you get "
* server:/home/youruser $ curl -H 'Content-Type:application/json' --data '{"where":{"trackId":{"$lt":19092}}, "select":{"_id":0, "trackId":1, "userId": 1}}' http://localhost:23456/tracks/get
* [some json]server:/home/youruser $"
* */
object sbtHTTP extends App with SimpleRoutingApp {

  val queryInterface = Option(System.getProperty("query.interface")).getOrElse("localhost")
  val queryPort = Try(System.getProperty("query.port").toInt).toOption.getOrElse(23456)
  val actorName = Option(System.getProperty("query.actor.name")).getOrElse("query-actor")
  private implicit val system = ActorSystem(actorName + "-system")

  startServer(queryInterface, queryPort, actorName) {
    path("tracks" / "get") {
      options {
        respondWithHeaders(
          HttpHeaders.RawHeader("Access-Control-Allow-Origin", "*"),
          HttpHeaders.RawHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Content-Type, Content-Range, Content-Disposition, Content-Description"),
          HttpHeaders.RawHeader("Access-Control-Allow-Methods", "POST")
        ) {
          complete("OK")
        }
      } ~
        post {
          respondWithHeaders(
            HttpHeaders.RawHeader("Access-Control-Allow-Origin", "*"),
            HttpHeaders.RawHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Content-Type, Content-Range, Content-Disposition, Content-Description"),
            HttpHeaders.RawHeader("Access-Control-Allow-Methods", "POST")
          ) {
            entity(as[queryRequest]) {
              queryRequest => complete {
                println(s"[${new Date()}] Received: $queryRequest")
                "[some json]"
              }
            }
          }
        }
    }
  }
}
