package model

import ai.minimax.{MaxActor, RequestMessage, ResponseMessage}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

trait PlayerModel {
  val name:String
  val sign:Char

  override def toString: String = this.sign.toString
}

case class RealPlayer(name: String = "player", sign: Char = 'x') extends PlayerModel

case class AIPlayer(override val name: String = "Computer", override val sign: Char = 'o') extends PlayerModel {
  val system: ActorSystem = ActorSystem("AISystem")
  val actor: ActorRef = system.actorOf(Props[MaxActor], "MaxActor")

  implicit val timeout: Timeout = Timeout(10 seconds)

  def getNextColumn(opponent:PlayerModel, matchfield:MatchfieldModel[PlayerModel], treeDepth:Int = 2): Int = {
    val future = actor ? RequestMessage(None, this, opponent, matchfield, treeDepth)
    Await.result(future, timeout.duration).asInstanceOf[ResponseMessage].columnNr.get
  }

}