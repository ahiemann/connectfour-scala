package controllers

import dsl.AutomaticMatchfieldImplicit._
import dsl.GameColumnImplicit._
import model.{PlayerModel, RoundModel, RoundResultGameOver, RoundResultMoveOk}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class GameLogicSpec extends AnyWordSpec with Matchers {
  "The GameLogic" should {
    val initialField = GameLogic.getInitialMatchField()
    val player1 = PlayerModel("Max Mustermann", 'x')
    val player2 = PlayerModel("Erika Mustermann", 'o')
    val player3 = PlayerModel("Hans Peter", 'o')
    val noPlayerPlayer = PlayerModel("NoPlayer", '-')
    val initialRoundModel = Success(RoundModel(0, initialField, player1))


    "return an initial match field" in {
      GameLogic.getInitialMatchField().toString should be("MatchfieldModel(Vector(Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return an Some(MatchfieldModel(...)) with one token" in {
      GameLogic.setChip(initialRoundModel).get.matchField.toString should be ("MatchfieldModel(Vector(Vector(x, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return a Failure if the target column was already full" in {
      val columnFullField = initialField.play(0 -> player1, 0 -> player2, 0 -> player1, 0 -> player2,0 -> player1, 0 -> player2)
      an [Exception] should be thrownBy GameLogic.setChip(Success(RoundModel(0, columnFullField, player1))).get
    }

    "return a Failure if the incoming Try[RoundResult] already was a Failure" in {
      an [Exception] should be thrownBy GameLogic.setChip(Failure(new Exception("Nevermind"))).get
    }

    "return a failure if a invalid column to insert the chip was selected" in {
      an [Exception] should be thrownBy GameLogic.setChip(Success(RoundModel(42, initialField, player1))).get
    }

    "return the next free row for token" in {
      val column = 0
      GameLogic.getNextEmptyRow(column, initialField) should be (Some(0))

      val round2Field = initialField.play(
        0 -> player1,
        0 -> player2
      )

      GameLogic.getNextEmptyRow(column, round2Field) should be (Some(2))
    }

    "return None if the column where the token should be placed is full" in {
      val fullColumnMatchfield = initialField.play(
        0 -> player1, 0 -> player2,
        0 -> player1, 0 -> player2,
        0 -> player1, 0 -> player2
      )

      GameLogic.getNextEmptyRow(0, fullColumnMatchfield) should be (None)
    }

    "should fail if the column where the token should be placed is outside of the range 0 to 5" in {
       an [Exception] should be thrownBy GameLogic.getNextEmptyRow(10, initialField)
    }

    "return a new Player with Name and sign" in {
      GameLogic.getInitialPlayerModel("Pascal", 'x').name should be ("Pascal")
    }

    "return with an empty list the value false" in {
      GameLogic.checkIfSomeoneWon(initialField, player1) should be (false)
    }

    "return an string output from matchfield" in {
      val players = Vector[PlayerModel](player1, player2)
      val currentMatchField = initialField.play(
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1)
      GameLogic.getMatchfieldOutput(players, currentMatchField) should be ("------- Connect Four  -------\n| Max Mustermann : x\n| Erika Mustermann : o\n--------------------------\nVector(-, -, -, -, -, -, -)\nVector(-, -, -, -, -, -, -)\nVector(-, -, -, -, -, -, -)\nVector(-, -, -, -, -, -, -)\nVector(-, -, -, -, -, -, -)\nVector(x, o, x, o, x, o, x)\n---------------------------\n      |1| 2| 3| 4| 5| 6| 7|")
    }

    "return the last free row for token" in {
      val column = 0

      val round5matchField = initialField.play(
        0 -> player1, 0 -> player2,
        0 -> player1,  0 -> player2,
        0 -> player1
      )
      GameLogic.getNextEmptyRow(column, round5matchField) should be (Some(5))
    }

    "return a Success that contains a RoundResultGameOver if the game is over for some reason" in {
      val matchfieldWithWinner = initialField.play(0 -> player1, 1 -> player1, 2 -> player1)
      GameLogic.playRound(Success(RoundModel(3, matchfieldWithWinner, player1))).get shouldBe a [RoundResultGameOver]
    }

    "return a Success with the value None if the game is not over yet" in {
      GameLogic.playRound(Success(RoundModel(0, initialField, player1))).get shouldBe a [RoundResultMoveOk]
    }

    "return a Failure if there was a failure while playing a round (i.e. setting the chip and checking if the game is over)" in {
      val fullColumnField = initialField.play(0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1)
      an [Exception] should be thrownBy GameLogic.playRound(Success(RoundModel(0, fullColumnField, player2))).get
    }

    "return a Failure if the incoming Try[RoundModel] already was a failure" in {
      an [Exception] should be thrownBy GameLogic.playRound(Failure(new Exception("Nevermind"))).get
    }

    "return true if the game is draw" in {

      val drawMatchField = initialField.play(
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
      )

      GameLogic.checkIfDraw(drawMatchField) should be (true)
    }

    "return false if the game is not a draw" in {
      val notDrawMatchField = initialField.play(
        0 -> noPlayerPlayer, 1 -> noPlayerPlayer, 2 -> noPlayerPlayer, 3 -> noPlayerPlayer, 4 -> noPlayerPlayer, 5 -> noPlayerPlayer, 6 -> noPlayerPlayer,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
      )

      GameLogic.checkIfDraw(notDrawMatchField) should be (false)
    }

    "return a 'The game is over, drawn' if that's the case" in {
      val drawMatchField = initialField.play(
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
      )

      GameLogic.checkIfGameIsOver(Success(RoundModel(0, drawMatchField, player1))).get.get should be ("The game is over, drawn.")
    }

    "return a string that mentions which player has won if that's the case" in {
      val winnerMatchfield = initialField.play(
        0 -> player1,
        1 -> player1, 2 -> player1, 3 -> player1, 4 -> player1,
        5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player2,
        2 -> player2, 3 -> player2,
        4 -> player2, 5 -> player1,
        6 -> player2, 0 -> player2
      )

      GameLogic.checkIfGameIsOver(Success(RoundModel(0, winnerMatchfield, player1))).get.get should be (s"Player ${player1.name} has won the game!")
    }

    "return a Success(None) if the game continues" in {
      val gameNotOverMatchfield = initialField.play(
        0-> player2, 1 -> player1
      )

      GameLogic.checkIfGameIsOver(Success(RoundModel(0, gameNotOverMatchfield, player1))) should be (Success(None))
    }

    "return a Failure if the input for checkIfGameIsOver already was a failure" in {
      an [Exception] should be thrownBy GameLogic.checkIfGameIsOver(Failure(new Exception("Not relevant"))).get
    }

    "return the number of successively tokens diagonal" in {
      val finalMatchField = initialField.play(
        0 -> player1,
        1 -> player1, 1 -> player1,
        2 -> player1, 2 -> player1, 2 -> player1,
        3 -> player3, 3 -> player1, 3 -> player1, 3 -> player1
      )

      GameLogic.countDiagonal(6,0,0,"",0,0, finalMatchField.rows.map(_.map(_.name))) should be (3)
    }

    "return the boolean of successively tokens diagonal" in{
      val finalMatchField = initialField.play(
        0 -> player1,
        1 -> player1, 1 -> player1,
        2 -> player1, 2 -> player1, 2 -> player1,
        3 -> player3, 3 -> player1, 3 -> player1, 3 -> player1
      )

      GameLogic.checkIfSomeoneWon(finalMatchField, player1) should be (true)
    }

    "return true if player won with 4 tokens horizontal" in {
      val finalMatchField = initialField.play(
        0 -> player1, 1 -> player1, 2 -> player1, 3 -> player1,
      )

      GameLogic.checkIfSomeoneWon(finalMatchField, player1) should be (true)
    }

    "return true if player won with 4 tokens vertical" in {
      val finalMatchField = initialField.play(
        0 -> player1,
        0 -> player1,
        0 -> player1,
        0 -> player1
      )

      GameLogic.checkIfSomeoneWon(finalMatchField, player1) should be (true)
    }

    "return true if 4 tokens are vertical from position 0|0 -> 3|3"  in {
      val finalMatchField = initialField.play(
        0 -> player1,
        1 -> player1, 1 -> player1,
        2 -> player1, 2 -> player1, 2 -> player1,
        3 -> player3, 3 -> player1, 3 -> player1, 3 -> player1
      )

      GameLogic.countDiagonal(6,0,0,"",0,0, finalMatchField.rows.map(_.map(_.name))) should be (3)
    }

    "return the boolean of unsuccessively tokens diagonal" in {
      val finalMatchField = initialField.play(
        1 -> player1,
        2 -> player1
      )

      GameLogic.checkIfSomeoneWon(finalMatchField, player1) should be (false)
    }

    "return a list with the empty columns" in {
      val finalMatchField = initialField.play(
        0 -> player1,
        1 -> player1, 1 -> player1,
        2 -> player1, 2 -> player1, 2 -> player1,
        3 -> player1, 3 -> player1, 3 -> player1, 3 -> player1,
        4 -> player1, 4 -> player1, 4 -> player1, 4 -> player1, 4 -> player1,
        5 -> player1, 5 -> player1, 5 -> player1, 5 -> player1, 5 -> player1, 5 -> player1,
        6 -> player1, 6 -> player1, 6 -> player1, 6 -> player1, 6 -> player1, 6 -> player1
      )

      GameLogic.getEmptyColumns(finalMatchField) should be (List(0,1,2,3,4))
    }
  }
}
