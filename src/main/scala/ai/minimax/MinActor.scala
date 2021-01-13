package ai.minimax

import akka.actor.Props
import akka.pattern.ask
import model.{MatchfieldModel, PlayerModel, RoundModel}
import util.GameLogic

import scala.concurrent.Future

class MinActor extends MiniMaxActor {

  override def makeScoreBasedChoice(choices: List[ResponseMessage]): ResponseMessage = choices.minBy(_.score)

  override def getPlayer(aiPlayer: PlayerModel, otherPlayer: PlayerModel): PlayerModel = {
    otherPlayer
  }

  override def spawnNewActor(columnNr: Int, matchField: MatchfieldModel[PlayerModel], aiPlayer: PlayerModel, otherPlayer: PlayerModel, depth: Int): Future[Any] = {
    val nextMatchfield = GameLogic.setChip(RoundModel(columnNr, matchField, otherPlayer)).get.matchfield
    context.actorOf(Props[MaxActor]) ? RequestMessage(Some(columnNr), aiPlayer, otherPlayer, nextMatchfield, depth)
  }
}
