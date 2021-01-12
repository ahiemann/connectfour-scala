package dsl

import model.{MatchfieldModel, PlayerModel, RoundModel}
import util.GameLogic

import scala.annotation.tailrec
import scala.util.Try

object AutomaticMatchfieldImplicit {
  implicit def convertMatchFieldToAutomaticMatchfield(matchfieldModel: MatchfieldModel[PlayerModel]): AutomaticMatchfield = new AutomaticMatchfield(matchfieldModel)


  implicit class AutomaticMatchfield(matchfield: MatchfieldModel[PlayerModel]) {
    def play(gameColumns: GameColumnPlayerMapping*): MatchfieldModel[PlayerModel] = {

      @tailrec
      def setChips(matchfield: MatchfieldModel[PlayerModel], gameColumns: Seq[GameColumnPlayerMapping]):MatchfieldModel[PlayerModel] = {
        if (gameColumns.isEmpty)
          matchfield
        else {
          val gameColumn = gameColumns.head
          val resultMatchfield = GameLogic.setChip(RoundModel(gameColumn.column, matchfield, gameColumn.player))
          setChips(resultMatchfield.get.matchfield, gameColumns.tail)
        }
      }
      
      setChips(matchfield, gameColumns)
    }
  }

}
