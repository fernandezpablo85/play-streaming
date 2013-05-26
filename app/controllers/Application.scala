package controllers

import play.api._
import play.api.libs.iteratee._
import play.api.libs.json._
import play.api.mvc._

import model.StatusUpdate

object Application extends Controller {

  implicit private val StatusWrites = Json.writes[StatusUpdate]

  val (stream, channel) = Concurrent.broadcast[StatusUpdate]

  val json = Enumeratee.map[StatusUpdate] { status => Json.toJson(status)}
  val readable = Enumeratee.map[JsValue] { obj => Json.stringify(obj) + "\n" }
  
  def index = Action {
    Ok("the firehose is live on /firehose")
  }


  def firehose = Action {
    val headers = Map("Content-Length" -> "-1", "Content-Type" -> "application/json")
    val header = ResponseHeader(200, headers)
    SimpleResult(header, stream.through(json).through(readable))
  }
}