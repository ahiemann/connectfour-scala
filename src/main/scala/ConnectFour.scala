import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object ConnectFour {

  def main(args:Array[String]): Unit = {

    val gameLogic = new GameLogic()

    // Create players
    println("Please type in name of player 1:")
    val name1 = StdIn.readLine()
    println("Please type in name of player 2:")
    val name2 = StdIn.readLine()

    val player1 = PlayerModel(name1, 'x')
    val player2 = PlayerModel(name2, 'o')
    println(s"Symbol of player ${player1.name} is ${player1.sign}")
    println(s"Symbol of player ${player2.name} is ${player2.sign}")
    val players = Vector[PlayerModel](player1, player2)

    // Get initial match field
    val matchField = gameLogic.getInitialMatchField()

    // get random index between 0 and 1
    val r = scala.util.Random
    val startPlayerIndex = r.nextInt(2)

    play(players, startPlayerIndex, matchField, gameLogic) match {
      case Some(pName) => println(s"Congratulations " + pName + "! You have won!")
      case None => println(s"Draw. The game is over.")
    }
  }

  @tailrec
  private def play(players: Vector[PlayerModel], playerIndex: Int, matchField: MatchfieldModel[PlayerModel], gameLogic: GameLogic): Option[String] = {
    val player = players(playerIndex)
    println(s"${player.name}, in which column should the chip be placed? ")

    // val columnIndex = StdIn.readInt()

    val roundModel = Try(StdIn.readInt()) match {
      case Success(columnIndexInt) =>
        val adaptedInt = columnIndexInt - 1 // our index starts at 0, the one for the user at 1
        Right(RoundModel(adaptedInt, matchField, player))
      case Failure(exception) => Left("Wrong input. Please type the number of the column where you would like to insert your chip")
    }

    val roundModelWithChipSet = gameLogic.setChip(roundModel)
    gameLogic.checkIfGameIsOver(roundModelWithChipSet) match {
      case Right(result) => result match {
        case Some(true) => Some(player.name) // return winner
        case Some(false) => None // No winner, but game is over
        case None => // No winner, no draw, game continues
          val matrix = roundModelWithChipSet.getOrElse(throw new Exception("No round data found")).matchField
          println("------- Connect Four  -------")
          println("| " + players(0).name + " : " + players(0).sign)
          println("| " + players(1).name + " : " + players(1).sign)
          println("--------------------------")
          println(matrix.rows(5))
          println(matrix.rows(4))
          println(matrix.rows(3))
          println(matrix.rows(2))
          println(matrix.rows(1))
          println(matrix.rows(0))
          println("---------------------------")
          println("      |1| 2| 3| 4| 5| 6| 7|")

          val nextPlayerIndex = if (playerIndex == 0) 1 else 0
          play(players, nextPlayerIndex, matrix, gameLogic)
      }
      case Left(result) =>
        // there was an issue. Output and restart round for same player
        println(result)
        play(players, playerIndex, matchField, gameLogic)
    }
  }

}
