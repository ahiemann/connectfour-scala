import controller.GameController
import model.{GameOverFlag, MatchfieldModel, PlayerModel}
import util.GameLogic
import view.Tui

import scala.annotation.tailrec

object ConnectFour {
  def main(args: Array[String]): Unit = {
    val startField = GameLogic.getInitialMatchField() // initiales Feld
    val player1 = PlayerModel(args(0), 'x')
    val player2 = PlayerModel(args(1), 'o')
    val view = new Tui()
    val controller = new GameController(view)
    val startPlayer = if (scala.util.Random.nextInt(2) == 0) player1 else player2

    @tailrec
    def playRound(view:Tui, controller:GameController, matchfield:MatchfieldModel[PlayerModel], player1:PlayerModel, player2:PlayerModel, currentPlayer:PlayerModel):Boolean = {
      controller.playRound(matchfield, player1, player2, currentPlayer) match {
        case Left(roundSuccessful) =>
          val nextPlayer = roundSuccessful._1
          val newMatchfield = roundSuccessful._2
          playRound(view, controller, newMatchfield, player1, player2, nextPlayer)
        case Right(GameOverFlag) => true // Game Over
      }
    }
    playRound(view, controller, startField, player1, player2, startPlayer)
  }
}