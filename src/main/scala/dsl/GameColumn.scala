package dsl

import model.PlayerModel

object GameColumn {
  implicit def convertIntToGameColumn(column: Int): GameColumn = new GameColumn(column)

  implicit class GameColumn(column: Int) {
    def ->(player:PlayerModel):GameColumnPlayerMapping = {
      GameColumnPlayerMapping(column, player)
    }
  }

}
