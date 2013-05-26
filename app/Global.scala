import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.concurrent._
import play.api.Play.current
import scala.concurrent.duration._
import scala.util.Random
import model.StatusUpdate

object Global extends GlobalSettings {

  val statuses = Array(
    StatusUpdate("pablo", "Are stream updates cool or what?"),
    StatusUpdate("hn_comments", "Nice tutorial!"),
    StatusUpdate("fake_profile", "lorem ipsum bla bla bla bla."),
    StatusUpdate("funes_mori", "please kill me before next match.")
  )

  override def onStart(app: Application) {
    import controllers.{Application => App}

    Akka.system.scheduler.schedule(0.seconds, 50.milliseconds) {
      App.channel.push(statuses(Random.nextInt(statuses.size)))
    }    
  }
}