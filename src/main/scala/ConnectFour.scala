import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel, RoundResultGameOver, RoundResultMoveOk}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object ConnectFour {

  def main(args: Array[String]): Unit = {

    // Create players
    val player1 = GameLogic.getInitialPlayerModel(args(0), 'x')
    val player2 = GameLogic.getInitialPlayerModel(args(1), 'o')

    println(s"Symbol of player ${player1.name} is ${player1.sign}")
    println(s"Symbol of player ${player2.name} is ${player2.sign}")

    // get random index between 0 and 1 and choose player that starts accordingly
    val startPlayer = if (scala.util.Random.nextInt(2) == 0) player1 else player2

    @tailrec
    def playRound(currentPlayer:PlayerModel, player1:PlayerModel, player2:PlayerModel, matchfield:MatchfieldModel[PlayerModel]):String = {
      println(GameLogic.getMatchfieldOutput(Vector(player1, player2), matchfield))
      println(s"${currentPlayer.name}, in which column should the chip be placed? ")

      val roundData = Try(StdIn.readInt()) match {

        case Success(inputIndex) =>
          val realIndex = inputIndex - 1
          Success(RoundModel(realIndex, matchfield, currentPlayer))

        case Failure(_) => Failure(new Exception("Wrong input. Please type the number of the column where you would like to insert your chip"))
      }

      GameLogic.playRound(roundData) match {
        case Success(r : RoundResultGameOver) => s"${r.gameOverReason}\n${r.matchfield}"
        case Success(r : RoundResultMoveOk) =>
          println(GameLogic.getMatchfieldOutput(Vector(player1, player2), r.matchfield))
          val nextPlayer = if (currentPlayer == player1) player2 else player1
          playRound(nextPlayer, player1, player2, r.matchfield)
        case Failure(f) =>
          println(f.getMessage)
          playRound(currentPlayer, player1, player2, matchfield)
        case _ => throw new Exception("Unknown RoundResult")
      }

    }

    // Start the game
    val gameOverMessage = playRound(startPlayer, player1, player2, GameLogic.getInitialMatchField())
    println(gameOverMessage)

  }
}
