package controllers

import model.{MatchfieldModel, PlayerModel, RoundModel}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class GameLogicSpec extends AnyWordSpec with Matchers {
  "The GameLogic" should {
    val initialField = GameLogic.getInitialMatchField()
    val player1 = PlayerModel("Max Mustermann", 'x')
    val player2 = PlayerModel("Erika Mustermann", 'o')
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
      val round1Result = GameLogic.setChip(initialRoundModel)
      val round2Result = GameLogic.setChip(round1Result)
      val round2Field = round2Result.get.matchField
      GameLogic.getNextEmptyRow(column, round2Field) should be (Some(2))
    }

    "return None if the column where the token should be placed is full" in {
      val fullColumn1 = Vector[Vector[PlayerModel]](
        Vector(player1, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(player1, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(player1, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(player1, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(player1, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(player1, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
      )
      val matchfield = MatchfieldModel(fullColumn1)
      GameLogic.getNextEmptyRow(0, matchfield) should be (None)
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

      val round5matchField = round5Result.get.matchField
      GameLogic.getNextEmptyRow(column, round5matchField) should be (Some(5))
    }

    "return true if the game is draw" in {
      val drawMatrix = Vector[Vector[PlayerModel]](
        Vector(player1, player2, player1, player2, player1, player2, player1),
        Vector(player2, player1, player2, player1, player2, player1, player2),
        Vector(player1, player2, player1, player2, player1, player2, player1),
        Vector(player2, player1, player2, player1, player2, player1, player2),
        Vector(player1, player2, player1, player2, player1, player2, player1),
        Vector(player2, player1, player2, player1, player2, player1, player2)
      )
      val drawMatchField = MatchfieldModel[PlayerModel](drawMatrix)

      GameLogic.checkIfDraw(drawMatchField) should be (true)
    }

    "return false if the game is not a draw" in {
      val notDrawMatrix = Vector[Vector[PlayerModel]](
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(player2, player1, player2, player1, player2, player1, player2),
        Vector(player1, player2, player1, player2, player1, player2, player1),
        Vector(player2, player1, player2, player1, player2, player1, player2),
        Vector(player1, player2, player1, player2, player1, player2, player1),
        Vector(player2, player1, player2, player1, player2, player1, player2)
      )
      val notDrawMatchField = MatchfieldModel[PlayerModel](notDrawMatrix)

      GameLogic.checkIfDraw(notDrawMatchField) should be (false)
    }

    "return a Success(Some(false)) if the game is over because it's a draw" in {
      val drawMatrix = Vector[Vector[PlayerModel]](
        Vector(player1, player2, player1, player2, player1, player2, player1),
        Vector(player2, player1, player2, player1, player2, player1, player2),
        Vector(player1, player2, player1, player2, player1, player2, player1),
        Vector(player2, player1, player2, player1, player2, player1, player2),
        Vector(player1, player2, player1, player2, player1, player2, player1),
        Vector(player2, player1, player2, player1, player2, player1, player2)
      )
      val drawMatchField = MatchfieldModel[PlayerModel](drawMatrix)

      GameLogic.checkIfGameIsOver(Success(RoundModel(0, drawMatchField, player1))) should be (Success(Some(false)))
    }

    "return a Success(Some(true)) if the game is over because a player won" in {
      val drawMatrix = Vector[Vector[PlayerModel]](
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(player1, player1, player1, player1, player2, player1, player2)
      )
      val winnerMatchField = MatchfieldModel[PlayerModel](drawMatrix)

      GameLogic.checkIfGameIsOver(Success(RoundModel(0, winnerMatchField, player1))) should be (Success(Some(true)))
    }

    "return a Success(None) if the game continues" in {
      val drawMatrix = Vector[Vector[PlayerModel]](
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
        Vector(player1, player2, player1, player2, player2, player1, player2)
      )
      val gameNotOverMatchfield = MatchfieldModel[PlayerModel](drawMatrix)

      GameLogic.checkIfGameIsOver(Success(RoundModel(0, gameNotOverMatchfield, player1))) should be (Success(None))
    }

    "return a Failure if the input for checkIfGameIsOver already was a failure" in {
      GameLogic.checkIfGameIsOver(Failure(new Exception("Not relevant"))) should be (Failure)
    }

    "return the number of successively tokens diagonal" in {
      val round1Result = GameLogic.setChip(initialRoundModel)
      val round1MatchField = round1Result.get.matchField

      val round2Result = GameLogic.setChip(Success(RoundModel(1, round1MatchField, player1)))
      val round2MatchField = round2Result.get.matchField

      val round3Result = GameLogic.setChip(Success(RoundModel(1, round2MatchField, player1)))
      val round3MatchField = round3Result.get.matchField

      val round4Result = GameLogic.setChip(Success(RoundModel(2, round3MatchField, player1)))
      val round4MatchField = round4Result.get.matchField

      val round5Result = GameLogic.setChip(Success(RoundModel(2, round4MatchField, player1)))
      val round5MatchField = round5Result.get.matchField

      val round6Result = GameLogic.setChip(Success(RoundModel(2, round5MatchField, player1)))
      val round6MatchField = round6Result.get.matchField

      val round7Result = GameLogic.setChip(Success(RoundModel(3, round6MatchField, PlayerModel("Hans Peter", 'o'))))
      val round7MatchField = round7Result.get.matchField

      val round8Result = GameLogic.setChip(Success(RoundModel(3, round7MatchField, player1)))
      val round8MatchField = round8Result.get.matchField

      val round9Result = GameLogic.setChip(Success(RoundModel(3, round8MatchField, player1)))
      val round9MatchField = round9Result.get.matchField

      val round10Result = GameLogic.setChip(Success(RoundModel(3, round9MatchField, player1)))
      val round10MatchField = round10Result.get.matchField

      GameLogic.countDiagonal(6,0,0,"",0,0, round10MatchField.rows.map(_.map(_.name))) should be (3)
    }

    "return the boolean of successively tokens diagonal" in{
      val round1Result = GameLogic.setChip(initialRoundModel)
      val round1MatchField = round1Result.get.matchField

      val round2Result = GameLogic.setChip(Success(RoundModel(1, round1MatchField, player1)))
      val round2MatchField = round2Result.get.matchField

      val round3Result = GameLogic.setChip(Success(RoundModel(1, round2MatchField, player1)))
      val round3MatchField = round3Result.get.matchField

      val round4Result = GameLogic.setChip(Success(RoundModel(2, round3MatchField, player1)))
      val round4MatchField = round4Result.get.matchField

      val round5Result = GameLogic.setChip(Success(RoundModel(2, round4MatchField, player1)))
      val round5MatchField = round5Result.get.matchField

      val round6Result = GameLogic.setChip(Success(RoundModel(2, round5MatchField, player1)))
      val round6MatchField = round6Result.get.matchField

      val round7Result = GameLogic.setChip(Success(RoundModel(3, round6MatchField, PlayerModel("Hans Peter", 'o'))))
      val round7MatchField = round7Result.get.matchField

      val round8Result = GameLogic.setChip(Success(RoundModel(3, round7MatchField, player1)))
      val round8MatchField = round8Result.get.matchField

      val round9Result = GameLogic.setChip(Success(RoundModel(3, round8MatchField, player1)))
      val round9MatchField = round9Result.get.matchField

      val round10Result = GameLogic.setChip(Success(RoundModel(3, round9MatchField, player1)))
      val round10MatchField = round10Result.get.matchField

      GameLogic.checkIfSomeoneWon(round10MatchField, player1) should be (true)
    }

    "return true if player won with 4 tokens horizontal" in {
      val round1Result = GameLogic.setChip(initialRoundModel)
      val round1MatchField = round1Result.get.matchField

      val round2Result = GameLogic.setChip(Success(RoundModel(1, round1MatchField, player1)))
      val round2MatchField = round2Result.get.matchField

      val round3Result = GameLogic.setChip(Success(RoundModel(2, round2MatchField, player1)))
      val round3MatchField = round3Result.get.matchField

      val round4Result = GameLogic.setChip(Success(RoundModel(3, round3MatchField, player1)))
      val round4MatchField = round4Result.get.matchField

      GameLogic.checkIfSomeoneWon(round4MatchField, player1) should be (true)
    }

    "return true if player won with 4 tokens vertical" in {
      val round1Result = GameLogic.setChip(initialRoundModel)
      val round1MatchField = round1Result.get.matchField

      val round2Result = GameLogic.setChip(Success(RoundModel(0, round1MatchField, player1)))
      val round2MatchField = round2Result.get.matchField

      val round3Result = GameLogic.setChip(Success(RoundModel(0, round2MatchField, player1)))
      val round3MatchField = round3Result.get.matchField

      val round4Result = GameLogic.setChip(Success(RoundModel(0, round3MatchField, player1)))
      val round4MatchField = round4Result.get.matchField

      GameLogic.checkIfSomeoneWon(round4MatchField, player1) should be (true)
    }

    "return true if 4 tokens are vertical from position 0|0 -> 3|3"  in {
      val round1Result = GameLogic.setChip(initialRoundModel)
      val round1MatchField = round1Result.get.matchField

      val round2Result = GameLogic.setChip(Success(RoundModel(1, round1MatchField, player1)))
      val round2MatchField = round2Result.get.matchField

      val round3Result = GameLogic.setChip(Success(RoundModel(1, round2MatchField, player1)))
      val round3MatchField = round3Result.get.matchField

      val round4Result = GameLogic.setChip(Success(RoundModel(2, round3MatchField, player1)))
      val round4MatchField = round4Result.get.matchField

      val round5Result = GameLogic.setChip(Success(RoundModel(2, round4MatchField, player1)))
      val round5MatchField = round5Result.get.matchField

      val round6Result = GameLogic.setChip(Success(RoundModel(2, round5MatchField, player1)))
      val round6MatchField = round6Result.get.matchField

      val round7Result = GameLogic.setChip(Success(RoundModel(3, round6MatchField, PlayerModel("Hans Peter", 'o'))))
      val round7MatchField = round7Result.get.matchField

      val round8Result = GameLogic.setChip(Success(RoundModel(3, round7MatchField, player1)))
      val round8MatchField = round8Result.get.matchField

      val round9Result = GameLogic.setChip(Success(RoundModel(3, round8MatchField, player1)))
      val round9MatchField = round9Result.get.matchField

      val round10Result = GameLogic.setChip(Success(RoundModel(3, round9MatchField, player1)))
      val round10MatchField = round10Result.get.matchField

      GameLogic.countDiagonal(6,0,0,"",0,0, round10MatchField.rows.map(_.map(_.name))) should be (3)
    }

    "return the boolean of unsuccessively tokens diagonal" in{
      val round1Result = GameLogic.setChip(initialRoundModel)
      val round1MatchField = round1Result.get.matchField

      val round2Result = GameLogic.setChip(Success(RoundModel(1, round1MatchField, player1)))
      val round2MatchField = round2Result.get.matchField

      GameLogic.checkIfSomeoneWon(round2MatchField, player1) should be (false)
    }
  }
}
