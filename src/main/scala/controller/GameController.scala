package controller

import model.{GameOverFlag, MatchfieldModel, PlayerModel, RoundModel}
import util.GameLogic
import view.Tui

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

class GameController(view:Tui) {
  def playRound(matchfield:MatchfieldModel[PlayerModel], player1:PlayerModel, player2:PlayerModel, currentPlayer:PlayerModel): Either[(PlayerModel, MatchfieldModel[PlayerModel]), GameOverFlag.type] = {
    view.outputPlayerLegend(player1, player2)
    view.outputMatchfield(matchfield)
    val column = getColumnForRound(currentPlayer)
    doRound(column, matchfield, player1, player2, currentPlayer)
  }

  @tailrec
  private def getColumnForRound(currentPlayer:PlayerModel) :Int = {
    view.outputNextTurn(currentPlayer)

    // hier wäre ein Unterscheidung nach dem Typ des Player möglich: Wenn es sich um den KI Player handelt, dann wird von diesem die Spaltenzahl aufgerufen
    view.getUserInput() match {
      case Success(inputIndex) =>
        val realIndex = inputIndex - 1
        realIndex
      case Failure(_) =>
        view.showError("Wrong input. Please type the number of the column where you would like to insert your chip")
        getColumnForRound(currentPlayer)
    }
  }

   def doRound(column:Int, matchfield:MatchfieldModel[PlayerModel], player1:PlayerModel, player2:PlayerModel, currentPlayer:PlayerModel) : Either[(PlayerModel, MatchfieldModel[PlayerModel]),GameOverFlag.type]= {
     val roundData = RoundModel(column, matchfield, currentPlayer)
    GameLogic.setChip(roundData) match {
      case Success(result) =>
        if (GameLogic.checkIfSomeoneWon(result.matchfield, currentPlayer)) {
          view.outputMatchfield(result.matchfield)
          view.announceWinner(currentPlayer)
          Right(GameOverFlag)
        }
        else if (GameLogic.checkIfDraw(result.matchfield)) {
          view.outputMatchfield(result.matchfield)
          view.annnounceDraw()
          Right(GameOverFlag)
        }
        else {
          // Spiel geht weiter
          val nextPlayer = if (currentPlayer == player1) player2 else player1
          Left((nextPlayer, result.matchfield))
        }
      case Failure(f) =>
        view.outputMatchfield(matchfield) // output previous matchfield again
        view.showError(f.getMessage) // show what was wrong (e.g. Column already full)
        Left((currentPlayer, matchfield))
    }
  }
}
