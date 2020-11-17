

import dsl.Insert
import dsl.AutomaticMatchfieldImplicit.AutomaticMatchfield
import dsl.GameColumnImplicit.GameColumn
import model.PlayerModel
import controllers.GameLogic


val player1 = PlayerModel("Max Mustermann", 'X')
val player2 = PlayerModel("Tom Blabla", 'O')

val matchfield = GameLogic.getInitialMatchField()

val result = matchfield play(1 -> player1, 2 -> player2, 1 -> player1)

val resMatchfield = Insert chipOf player1 inColumn 1 ofMatchfield matchfield
Insert chipOf player2 inColumn 0 ofMatchfield resMatchfield.get.matchField