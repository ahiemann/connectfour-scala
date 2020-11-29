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
  println("New MinActor")

  override def makeScoreChoice(choices: List[ResponseMessage]): Int = choices.map(e => e.score).max

  override def getPlayer(aiPlayer: PlayerModel, otherPlayer: PlayerModel): PlayerModel = {
    otherPlayer
  }

  override def spawnNewActor(columnNr: Int, matchField: MatchfieldModel[PlayerModel], aiPlayer: PlayerModel, otherPlayer: PlayerModel, depth: Int): Future[Any] = {
    val nextMatchfield = GameLogic.setChip(Success(RoundModel(columnNr, matchField, otherPlayer))).get.matchField
    context.actorOf(Props[MaxActor]) ? RequestMessage(aiPlayer, otherPlayer, nextMatchfield, depth)
  }
}
