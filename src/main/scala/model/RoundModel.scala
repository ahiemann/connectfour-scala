package model

case class RoundModel(val columnIndex:Int, val matchfield: MatchfieldModel[PlayerModel], val player: PlayerModel)
