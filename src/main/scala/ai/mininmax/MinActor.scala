package ai.mininmax

import java.util.concurrent.TimeUnit

import akka.actor.TypedActor.dispatcher
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.concurrent.Future
import scala.util.Success

class MinActor extends MiniMaxActor {
  override def spawnNewChildActor(column:Int, aiPlayer: PlayerModel, otherPlayer: PlayerModel, matchfield: MatchfieldModel[PlayerModel], depth: Int): Future[Any] = {
    val nextMatchfield = GameLogic.setChip(Success(RoundModel(column, matchfield, aiPlayer))).get.matchField
    context.actorOf(Props[MaxActor]) ? RequestMessage(aiPlayer, otherPlayer, nextMatchfield, depth)
  }

  override def makeScoreChoice(choices: List[ResponseMessage]): Int = {
    choices.map(e => e.score).max
  }
}
