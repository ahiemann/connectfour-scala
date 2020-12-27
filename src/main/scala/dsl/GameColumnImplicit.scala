package dsl

import model.PlayerModel
import scala.language.implicitConversions

object GameColumnImplicit:
  implicit def convertIntToGameColumn(column: Int): GameColumn = new GameColumn(column)

  implicit class GameColumn(column: Int) {
    def ->(player:PlayerModel):GameColumnPlayerMapping = {
      GameColumnPlayerMapping(column, player)
    }
  }
