package dsl

import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

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
          val resultMatchfield = GameLogic.setChip(Try(RoundModel(gameColumn.column, matchfield, gameColumn.player)))
          setChips(resultMatchfield.get.matchField, gameColumns.tail)
        }
      }
      
      setChips(matchfield, gameColumns)
    }
  }

}
