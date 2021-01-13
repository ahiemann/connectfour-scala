

import dsl.Insert
import dsl.GameColumnImplicit.GameColumn
import model.{PlayerModel, RealPlayer}
import util.{AutomaticMatchfield, GameLogic}


val player1 = RealPlayer("Max Mustermann", 'X')
val player2 = RealPlayer("Tom Blabla", 'O')

val matchfield = GameLogic.getInitialMatchField()
val automaticMatchfield = new AutomaticMatchfield(matchfield)

val result = automaticMatchfield play(1 -> player1, 2 -> player2, 1 -> player1)

val resMatchfield = Insert chipOf player1 inColumn 1 ofMatchfield matchfield
Insert chipOf player2 inColumn 0 ofMatchfield resMatchfield.get.matchfield