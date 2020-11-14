
import dsl.AutomaticMatchField.AutomaticMatchfield
import dsl.GameColumn.GameColumn
import model.PlayerModel
import controllers.GameLogic

val player1 = PlayerModel("Max Mustermann", 'X')
val player2 = PlayerModel("Tom Blabla", 'O')

val matchfield = GameLogic.getInitialMatchField()

val result = matchfield play(1 -> player1, 2 -> player2, 1 -> player1)