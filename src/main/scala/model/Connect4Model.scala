package model

import dsl.GameColumnPlayerMapping

case class Connect4Model(player1: PlayerModel, player2: PlayerModel, roundData: List[GameColumnPlayerMapping])
