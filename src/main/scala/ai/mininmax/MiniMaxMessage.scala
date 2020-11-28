package ai.mininmax

import model.{MatchfieldModel, PlayerModel}

trait MiniMaxMessage
case class RequestMessage(simulatedPlayer:PlayerModel, otherPlayer:PlayerModel, matchfield:MatchfieldModel[PlayerModel], depth:Int) extends MiniMaxMessage
case class ResponseMessage(score:Int) extends MiniMaxMessage
