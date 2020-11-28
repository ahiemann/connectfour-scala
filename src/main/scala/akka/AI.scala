package akka

import java.util.concurrent.TimeUnit

import ai.mininmax.{MaxActor, RequestMessage}
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import controllers.GameLogic
import model.PlayerModel

import scala.concurrent.Await



object Main extends App {
  val system = ActorSystem("AISystem")
  val actor = system.actorOf(Props[MaxActor], "MaxActor")

  val playerAI = PlayerModel("Computer", 'X')
  val playerHuman = PlayerModel("Human", 'O')
  val matchfield = GameLogic.getInitialMatchField()


  actor ! "Hallo Welt"
  // actor ! 42
  implicit val timeout: Timeout = Timeout(2, TimeUnit.SECONDS)
  val future = actor ? RequestMessage(playerAI, playerHuman, matchfield, 0)


  val result = Await.result(future, timeout.duration)
  println(result)
}
