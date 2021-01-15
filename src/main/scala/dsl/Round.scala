package dsl

import util.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.util.Try

object Insert:
  def chipOf(player:PlayerModel): ColumnMediator = ColumnMediator(player)

class ColumnMediator(val player:PlayerModel):
  def inColumn(colIndex: Int):MatchfieldMediator = MatchfieldMediator(colIndex, player)

class MatchfieldMediator(colIndex: Int, player: PlayerModel):
  def ofMatchfield(matchfield: MatchfieldModel[PlayerModel]):Try[RoundModel] = GameLogic.setChip(RoundModel(colIndex, matchfield, player))

