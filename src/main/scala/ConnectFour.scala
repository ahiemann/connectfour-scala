import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel}

import scala.annotation.tailrec
import scala.io.StdIn

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

    // get randim index between 0 and 1
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
    printf(s"${player.name}, in which column should the chip be placed? ")
    val columnIndex = StdIn.readInt()

    gameLogic.setChip(columnIndex, matchField, player) match {
      case None =>
        println("Selected column is already full. Please select another column to place chip")
        play(players, playerIndex, matchField, gameLogic)

      case Some(matrix) =>
        println("------- 4 GEWINNT  -------")
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
        println("      |0| 1| 2| 3| 4| 5| 6|")

        gameLogic.checkIfSomeoneWon(matrix, player) match {
          // return winner
          case Some(true) => Some(player.name)
          case Some(false) =>
            if(gameLogic.checkIfDraw(matchField)) {
              // no winner, but game is over
              None
            }
            else {
              // Game not over yet, recurse
              // toggle playerIndex
              val nextPlayerIndex = if (playerIndex == 0) 1 else 0
              play(players, nextPlayerIndex, matrix, gameLogic)
            }
        }
    }
  }
}

