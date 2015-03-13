package com.acme.scala

import spray.http.MediaTypes._
import spray.httpx.unmarshalling.Unmarshaller
import spray.http.HttpEntity
import spray.json._

case class queryRequest (where: JsObject, select: JsObject, limit: Option[Int])

object queryRequest {

  object JsonProtocol extends DefaultJsonProtocol {
    implicit val queryFormat = jsonFormat3(queryRequest.apply)
  }

  implicit val queryRequestUnmarshaller =
    Unmarshaller[queryRequest](`application/json`) {
      case HttpEntity.NonEmpty(contentType, data) => {
        import JsonProtocol._
        data.asString.parseJson.convertTo[queryRequest]
      }
    }
}
