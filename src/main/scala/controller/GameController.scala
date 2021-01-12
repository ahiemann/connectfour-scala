package controller

import model.{GameOverFlag, MatchfieldModel, PlayerModel, RoundModel}
import util.GameLogic
import view.Tui

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

class GameController(view:Tui) {
  def playRound(matchfield:MatchfieldModel[PlayerModel], player1:PlayerModel, player2:PlayerModel, currentPlayer:PlayerModel): Either[(PlayerModel, MatchfieldModel[PlayerModel]), GameOverFlag.type] = {
    view.outputMatchfield(matchfield)
    view.outputPlayerLegend(player1, player2)
    val column = getColumnForRound(currentPlayer)
    doRound(column, matchfield, player1, player2, currentPlayer)
  }

  // TODO: Consider this https://stackoverflow.com/a/29479517 for testing
  @tailrec
  private def getColumnForRound(currentPlayer:PlayerModel) :Int = {
    view.outputNextTurn(currentPlayer)
    val invalidInputMessage = "Invalid input. Please type the number of the column where you would like to insert your chip"

    // hier wäre ein Unterscheidung nach dem Typ des Player möglich: Wenn es sich um den KI Player handelt, dann wird von diesem die Spaltenzahl aufgerufen
    view.getUserInput() match {
      case Success(inputIndex) =>
        // TODO: Remove other check in util.Gamelogic
        if (!(1 to 7 contains inputIndex)) {
          view.showError(invalidInputMessage)
          getColumnForRound(currentPlayer)
        }
        else {
          val realIndex = inputIndex - 1
          realIndex
        }
      case Failure(_) =>
        view.showError(invalidInputMessage)
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
