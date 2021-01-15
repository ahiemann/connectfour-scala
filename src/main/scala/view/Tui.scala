package view

import model.{MatchfieldModel, PlayerModel}

import scala.io.StdIn
import scala.util.Try

class Tui:
  def showStart(): Unit = println("Welcome to Connect 4!")

  def askForPlayerName(playerNr:Int): Unit = println(s"Please provide name for player $playerNr:")

  def outputMatchfield(field:MatchfieldModel[PlayerModel]): Unit = println(field)

  def outputPlayerLegend(player1:PlayerModel, player2:PlayerModel): Unit =
    println(s"""|${player1.name}: ${player1.sign}
      |${player2.name}: ${player2.sign}
      |""".stripMargin)

  def outputNextTurn(player:PlayerModel): Unit = println(s"${player.name}, where do you want to put the chip?")

  def announceWinner(player:PlayerModel): Unit = println(s"${player.name} (${player.sign}) has won the game!")

  def annnounceDraw(): Unit = println("The game ended in a draw")

  def showError(msg:String): Unit = println(msg)

  def getUserInputInt:Try[Int] = Try(StdIn.readInt())

  def getUserInputString :String = StdIn.readLine()
