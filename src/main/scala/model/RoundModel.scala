package model

case class RoundModel(val columnIndex:Int, val matchField: MatchfieldModel[PlayerModel], val player: PlayerModel)
