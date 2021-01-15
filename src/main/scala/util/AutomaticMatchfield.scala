package util

import dsl.GameColumnPlayerMapping
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.annotation.tailrec

class AutomaticMatchfield(matchfield: MatchfieldModel[PlayerModel]):
  def play(gameColumns: GameColumnPlayerMapping*): MatchfieldModel[PlayerModel] =

    @tailrec
    def setChips(matchfield: MatchfieldModel[PlayerModel], gameColumns: Seq[GameColumnPlayerMapping]):MatchfieldModel[PlayerModel] =
      if gameColumns.isEmpty then
        matchfield
      else
        val gameColumn = gameColumns.head
        val resultMatchfield = GameLogic.setChip(RoundModel(gameColumn.column, matchfield, gameColumn.player))
        setChips(resultMatchfield.get.matchfield, gameColumns.tail)
      
    setChips(matchfield, gameColumns)
  
