package ai.mininmax

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.concurrent.Future
import scala.util.Success


class MaxActor extends MiniMaxActor {

  override def spawnNewChildActor(column:Int, aiPlayer: PlayerModel, otherPlayer: PlayerModel, matchfield: MatchfieldModel[PlayerModel], depth: Int): Future[Any] = {
    val nextMatchfield = GameLogic.setChip(Success(RoundModel(column, matchfield, otherPlayer))).get.matchField
    context.actorOf(Props[MinActor]) ? RequestMessage(aiPlayer, otherPlayer, nextMatchfield, depth)
  }

  override def makeScoreChoice(choices: List[ResponseMessage]): Int = {
    choices.map(e => e.score).max
  }

}
