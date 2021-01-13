package ai.minimax

import java.util.concurrent.TimeUnit
import akka.actor.TypedActor.dispatcher
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import model.{MatchfieldModel, PlayerModel, RoundModel}
import util.GameLogic

import scala.concurrent.Future
import scala.util.Success

class MinActor extends MiniMaxActor {
  //println("New MinActor")

  override def makeScoreBasedChoice(choices: List[ResponseMessage]): ResponseMessage = choices.minBy(_.score)

  override def getPlayer(aiPlayer: PlayerModel, otherPlayer: PlayerModel): PlayerModel = {
    otherPlayer
  }

  override def spawnNewActor(columnNr: Int, matchField: MatchfieldModel[PlayerModel], aiPlayer: PlayerModel, otherPlayer: PlayerModel, depth: Int): Future[Any] = {
    val nextMatchfield = GameLogic.setChip(RoundModel(columnNr, matchField, otherPlayer)).get.matchfield
    context.actorOf(Props[MaxActor]) ? RequestMessage(Some(columnNr), aiPlayer, otherPlayer, nextMatchfield, depth)
  }
}