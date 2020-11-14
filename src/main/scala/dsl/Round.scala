package dsl

import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.util.Try

object Insert {
  def chipOf(player:PlayerModel): ColumnMediator = new ColumnMediator(player)
}

class ColumnMediator(val player:PlayerModel) {
  def inColumn(colIndex: Int):MatchfieldMediator = new MatchfieldMediator(colIndex, player)
}

class MatchfieldMediator(colIndex: Int, player: PlayerModel) {
  def ofMatchfield(matchfield: MatchfieldModel[PlayerModel]):Try[RoundModel] = {
    GameLogic.setChip(Try(RoundModel(colIndex, matchfield, player)))
  }
}

