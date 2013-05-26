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
  val severSentEvents = Enumeratee.map[JsValue] {obj => "data: " + Json.stringify(obj) + "\n\n"}
  
  def index = Action {
    Ok("the firehose is live on /firehose, the SSE stream is on /events")
  }

  def events = Action {
    Ok(views.html.Sse())
  }

  def sse = Action {
    val headers = Map("Content-Length" -> "-1", "Connection" -> "close", "Content-Type" -> "text/event-stream")
    val header = ResponseHeader(200, headers)
    SimpleResult(header, stream &> json &> severSentEvents)
  }

  def firehose = Action {
    val headers = Map("Content-Length" -> "-1", "Connection" -> "close","Content-Type" -> "application/json")
    val header = ResponseHeader(200, headers)
    SimpleResult(header, stream.through(json).through(readable))
  }
}