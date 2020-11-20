package model

case class RoundResultGameOver(matchfield: MatchfieldModel[PlayerModel], gameOverReason:String) extends RoundResult

