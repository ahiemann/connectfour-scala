package controllers

import dsl.AutomaticMatchfieldImplicit.AutomaticMatchfield
import dsl.GameColumnImplicit.GameColumn
import model.{MatchfieldModel, PlayerModel, RoundModel}
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

    "return the last free row for token" in {
      val column = 0
      val round1Result = GameLogic.setChip(initialRoundModel)
      val round2Result = GameLogic.setChip(round1Result)
      val round3Result = GameLogic.setChip(round2Result)
      val round4Result = GameLogic.setChip(round3Result)
      val round5Result = GameLogic.setChip(round4Result)

      val round5matchField = initialField.play(
        0 -> player1, 0 -> player2,
        0 -> player1,  0 -> player2,
        0 -> player1
      )
      GameLogic.getNextEmptyRow(column, round5matchField) should be (Some(5))
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

    "return a Success(Some(false)) if the game is over because it's a draw" in {
      val drawMatchField = initialField.play(
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player1, 1 -> player2, 2 -> player1, 3 -> player2, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player1, 2 -> player2, 3 -> player1, 4 -> player2, 5 -> player1, 6 -> player2,
      )

      GameLogic.checkIfGameIsOver(Success(RoundModel(0, drawMatchField, player1))) should be (Success(Some(false)))
    }

    "return a Success(Some(true)) if the game is over because a player won" in {
      val winnerMatchfield = initialField.play(
        0 -> player1, 1 -> player1, 2 -> player1, 3 -> player1, 4 -> player1, 5 -> player2, 6 -> player1,
        0 -> player2, 1 -> player2, 2 -> player2, 3 -> player2, 4 -> player2, 5 -> player1, 6 -> player2,
        0 -> player2,
        0 -> player2,
        0 -> player2
      )

      GameLogic.checkIfGameIsOver(Success(RoundModel(0, winnerMatchfield, player1))) should be (Success(Some(true)))
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
  }
}
