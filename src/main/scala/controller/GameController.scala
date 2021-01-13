package controller

import model.{AIPlayer, GameOverFlag, MatchfieldModel, PlayerModel, RealPlayer, RoundModel}
import util.GameLogic
import view.Tui

import scala.annotation.tailrec
import scala.util.{Failure, Success}

class GameController(view:Tui) {
  @tailrec
  final def startGame() :(PlayerModel, PlayerModel, MatchfieldModel[PlayerModel])= {
    view.showStart()
    val matchfield = GameLogic.getInitialMatchField

    view.getUserInputInt match {
      case Success(mode) =>
        if (1 to 2 contains mode) {
          val player1Name = getPlayerName(1)
          val player1 = RealPlayer(player1Name)
          val player2 = if (mode == 1) {
            AIPlayer()
          } else {
            val player2Name = getPlayerName(2)
            RealPlayer(player2Name, 'o')
          }
          (player1, player2, matchfield)

        }
        else {
          view.showError("Invalid mode selected")
          startGame()
        }
      case Failure(_) =>
        view.showError("Invalid input for game mode selection")
        startGame()
    }
  }

  private def getPlayerName(playerNr : Int):String = {
    view.askForPlayerName(playerNr)
    view.getUserInputString
  }

  def playRound(matchfield:MatchfieldModel[PlayerModel], player1:PlayerModel, player2:PlayerModel, currentPlayer:PlayerModel): Either[(PlayerModel, MatchfieldModel[PlayerModel]), GameOverFlag.type] = {
    view.outputMatchfield(matchfield)
    view.outputPlayerLegend(player1, player2)
    val currentOpponent = if (currentPlayer == player1) player2 else player1
    val column = getColumnForRound(currentPlayer, currentOpponent, matchfield)
    doRound(column, matchfield, player1, player2, currentPlayer)
  }

  // TODO: Consider this https://stackoverflow.com/a/29479517 for testing
  @tailrec
  private def getColumnForRound(currentPlayer:PlayerModel, opponent:PlayerModel, currentMatchfield:MatchfieldModel[PlayerModel]) :Int = {
    val invalidInputMessage = "Invalid input. Please type the number of the column where you would like to insert your chip"

    currentPlayer match {
      case p:AIPlayer => p.getNextColumn(opponent, currentMatchfield)

      case realPlayer =>
        view.outputNextTurn(currentPlayer)
        view.getUserInputInt match {
        case Success(inputIndex) =>

          if (1 to 7 contains inputIndex) {
            val realIndex = inputIndex - 1
            realIndex
          }
          else {
            view.showError(invalidInputMessage)
            getColumnForRound(realPlayer, opponent, currentMatchfield)
          }
        case Failure(_) =>
          view.showError(invalidInputMessage)
          getColumnForRound(realPlayer, opponent, currentMatchfield)
      }
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
