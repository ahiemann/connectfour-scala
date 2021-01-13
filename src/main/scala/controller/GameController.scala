package controller

import model.{AIPlayer, GameOverFlag, MatchfieldModel, PlayerModel, RealPlayer, RoundModel}
import util.GameLogic
import view.Tui

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

class GameController(view:Tui) {
  @tailrec
  final def startGame() :(PlayerModel, PlayerModel, MatchfieldModel[PlayerModel])= {
    view.showStart()
    val matchfield = GameLogic.getInitialMatchField()

    view.getUserInputInt() match {
      case Success(mode) =>
        if (!(1 to 2 contains mode)) {
          view.showError("Invalid mode selected")
          startGame()
        }
        else {
          val player1Name = getPlayerName(1)
          val player1 = RealPlayer(player1Name, 'x')
          val player2 = if (mode == 1) {
            AIPlayer(sign = 'o')
          } else {
            val player2Name = getPlayerName(2)
            RealPlayer(player2Name, 'o')
          }
          (player1, player2, matchfield)
        }
      case Failure(e) =>
        view.showError("Invalid input for game mode selection")
        startGame()
    }
  }

  @tailrec
  private def getPlayerName(playerNr : Int):String = {
    view.askForPlayerName(playerNr)
    view.getUserInputString() match {
      case Success(name) => name
      case Failure(exception) =>
        view.showError("Invalid input for player name!")
        getPlayerName(playerNr)
    }
  }

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
    view.getUserInputInt() match {
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
