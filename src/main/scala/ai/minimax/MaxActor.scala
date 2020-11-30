package ai.minimax

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.concurrent.Future
import scala.util.Success


class MaxActor extends MiniMaxActor {
  //println("New MaxActor")

  override def makeScoreBasedChoice(choices: List[ResponseMessage]): ResponseMessage = choices.maxBy(_.score)

  override def getPlayer(aiPlayer: PlayerModel, otherPlayer: PlayerModel): PlayerModel = {
    aiPlayer
  }

  override def spawnNewActor(columnNr: Int, matchField: MatchfieldModel[PlayerModel], aiPlayer: PlayerModel, otherPlayer: PlayerModel, depth: Int): Future[Any] = {
    val nextMatchfield = GameLogic.setChip(Success(RoundModel(columnNr, matchField, aiPlayer))).get.matchField
    context.actorOf(Props[MinActor]) ? RequestMessage(Some(columnNr), aiPlayer, otherPlayer, nextMatchfield, depth)
  }
}
