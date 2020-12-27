package dsl

import model.PlayerModel

object GameColumnImplicit:
  extension(column:Int)
    def ->(player:PlayerModel):GameColumnPlayerMapping = GameColumnPlayerMapping(column, player)
