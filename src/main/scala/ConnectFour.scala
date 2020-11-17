import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object ConnectFour {

  def main(args:Array[String]): Unit = {

    // Create players
    val player1 = GameLogic.getInitialPlayerModel(args(0),'x')
    val player2 = GameLogic.getInitialPlayerModel(args(1),'o')

    println(s"Symbol of player ${player1.name} is ${player1.sign}")
    println(s"Symbol of player ${player2.name} is ${player2.sign}")
    val players = Vector[PlayerModel](player1, player2)

    // Get initial match field
    val matchField = GameLogic.getInitialMatchField()

    // get random index between 0 and 1
    val r = scala.util.Random
    val startPlayerIndex = r.nextInt(2)

    play(players, startPlayerIndex, matchField) match {
      case Some(pName) => println(s"Congratulations $pName! You have won!")
      case None => println(s"Draw. The game is over.")
    }
  }

  @tailrec
  private def play(players: Vector[PlayerModel], playerIndex: Int, matchField: MatchfieldModel[PlayerModel]): Option[String] = {
    val player = players(playerIndex)
    println(s"${player.name}, in which column should the chip be placed? ")
    val roundModel = Try(StdIn.readInt()) match {
      case Success(columnIndexInt) =>
        val adaptedInt = columnIndexInt - 1 // our index starts at 0, the one for the user at 1
        Success(RoundModel(adaptedInt, matchField, player))
      case Failure(_) => Failure(new Exception("Wrong input. Please type the number of the column where you would like to insert your chip"))
    }

    val roundModelWithChipSet = GameLogic.setChip(roundModel)
    GameLogic.checkIfGameIsOver(roundModelWithChipSet) match {
      case Success(result) => result match {
        case Some(true) => Some(player.name) // return winner
        case Some(false) => None // No winner, but game is over
        case None => // No winner, no draw, game continues
          val matrix = roundModelWithChipSet.getOrElse(throw new Exception("No round data found")).matchField
          println(GameLogic.getMatchfieldOutput(players, matrix))
          val nextPlayerIndex = if (playerIndex == 0) 1 else 0
          play(players, nextPlayerIndex, matrix)
      }
      case Failure(result) =>
        // there was an issue. Output and restart round for same player
        println(result.getMessage)
        play(players, playerIndex, matchField)
    }
  }
}
