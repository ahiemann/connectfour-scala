package view

import model.{MatchfieldModel, PlayerModel}

import scala.io.StdIn
import scala.util.Try

class Tui {
  def outputMatchfield(field:MatchfieldModel[PlayerModel]) = {
    println(field)
  }

  def outputPlayerLegend(player1:PlayerModel, player2:PlayerModel) = {
    println(s"""|${player1.name}: ${player1.sign}
      |${player2.name}: ${player2.sign}
      |""".stripMargin)
  }

  def outputNextTurn(player:PlayerModel) = {
    println(s"Player ${player.name}, where do you want to put the chip?")
  }

  def announceWinner(player:PlayerModel) = {
    println(s"Player ${player.name} (${player.sign}) has won the game!")
  }

  def annnounceDraw() = {
    println("The game ended in a draw")
  }

  def showError(msg:String) = {
    println(msg)
  }

  def getUserInput() :Try[Int] = {
    Try(StdIn.readInt())
  }
}
