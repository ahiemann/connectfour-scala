package ai.minimax

import model.{MatchfieldModel, PlayerModel}

trait MiniMaxMessage
case class RequestMessage(columnNr:Option[Int], simulatedPlayer:PlayerModel, otherPlayer:PlayerModel, matchfield:MatchfieldModel[PlayerModel], depth:Int) extends MiniMaxMessage
case class ResponseMessage(columnNr:Option[Int], score:Int) extends MiniMaxMessage
