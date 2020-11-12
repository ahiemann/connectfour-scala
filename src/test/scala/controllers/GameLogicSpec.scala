package controllers

import model.{MatchfieldModel, PlayerModel, RoundModel}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Success

class GameLogicSpec extends AnyWordSpec with Matchers {
  "The GameLogic" should {
    val gameLogic = new GameLogic()
    val initialField = gameLogic.getInitialMatchField()
    val player1 = PlayerModel("Max Mustermann", 'x')
    val player2 = PlayerModel("Erika Mustermann", 'o')
    val noPlayerPlayer = PlayerModel("NoPlayer", '-')
    val initialRoundModel = Success(RoundModel(0, initialField, player1))


    "return an initial match field" in {
      gameLogic.getInitialMatchField().toString should be("MatchfieldModel(Vector(Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return an Some(MatchfieldModel(...)) with one token" in {
      gameLogic.setChip(initialRoundModel).get.matchField.toString should be ("MatchfieldModel(Vector(Vector(x, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return the next free row for token" in {
      val column = 0
      gameLogic.getNextEmptyRow(column, initialField) should be (Some(0))
      val round1Result = gameLogic.setChip(initialRoundModel)
      val round2Result = gameLogic.setChip(round1Result)
      val round2Field = round2Result.get.matchField
      gameLogic.getNextEmptyRow(column, round2Field) should be (Some(2))
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
      gameLogic.getNextEmptyRow(0, matchfield) should be (None)
    }

    "should fail if the column where the token should be placed is outside of the range 0 to 5" in {
       an [Exception] should be thrownBy gameLogic.getNextEmptyRow(10, initialField)
    }

    "return the last free row for token" in {
      val column = 0
      val round1Result = gameLogic.setChip(initialRoundModel)
      val round2Result = gameLogic.setChip(round1Result)
      val round3Result = gameLogic.setChip(round2Result)
      val round4Result = gameLogic.setChip(round3Result)
      val round5Result = gameLogic.setChip(round4Result)

      val round5matchField = round5Result.get.matchField
      gameLogic.getNextEmptyRow(column, round5matchField) should be (Some(5))
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

      gameLogic.checkIfDraw(drawMatchField) should be (true)
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

      gameLogic.checkIfDraw(notDrawMatchField) should be (false)
    }

    "return the number of successively tokens diagonal" in {
      val round1Result = gameLogic.setChip(initialRoundModel)
      val round1MatchField = round1Result.get.matchField

      val round2Result = gameLogic.setChip(Success(RoundModel(1, round1MatchField, player1)))
      val round2MatchField = round2Result.get.matchField

      val round3Result = gameLogic.setChip(Success(RoundModel(1, round2MatchField, player1)))
      val round3MatchField = round3Result.get.matchField

      val round4Result = gameLogic.setChip(Success(RoundModel(2, round3MatchField, player1)))
      val round4MatchField = round4Result.get.matchField

      val round5Result = gameLogic.setChip(Success(RoundModel(2, round4MatchField, player1)))
      val round5MatchField = round5Result.get.matchField

      val round6Result = gameLogic.setChip(Success(RoundModel(2, round5MatchField, player1)))
      val round6MatchField = round6Result.get.matchField

      val round7Result = gameLogic.setChip(Success(RoundModel(3, round6MatchField, PlayerModel("Hans Peter", 'o'))))
      val round7MatchField = round7Result.get.matchField

      val round8Result = gameLogic.setChip(Success(RoundModel(3, round7MatchField, player1)))
      val round8MatchField = round8Result.get.matchField

      val round9Result = gameLogic.setChip(Success(RoundModel(3, round8MatchField, player1)))
      val round9MatchField = round9Result.get.matchField

      val round10Result = gameLogic.setChip(Success(RoundModel(3, round9MatchField, player1)))
      val round10MatchField = round10Result.get.matchField

      gameLogic.countDiagonal(6,0,0,"",0,0, round10MatchField.rows.map(_.map(_.name))) should be (4)
    }

    "return the boolean of successively tokens diagonal" in{
      val move1 = gameLogic.setChip(0, initialField, player).get
      val move2 = gameLogic.setChip(1, move1, player).get
      val move3 = gameLogic.setChip(1, move2, player).get
      val move4 = gameLogic.setChip(2, move3, player).get
      val move5 = gameLogic.setChip(2, move4, player).get
      val move6 = gameLogic.setChip(2, move5, player).get
      val move7 = gameLogic.setChip(3, move6, new PlayerModel("Hans Peter", 'o')).get
      val move8 = gameLogic.setChip(3, move7, player).get
      val move9 = gameLogic.setChip(3, move8, player).get
      val move10 = gameLogic.setChip(3, move9, player).get
      gameLogic.checkIfSomeoneWon(move10, player) should be (Some(true))
    }

    "return true if player won with 4 tokens horizontal" in {
      val move1 = gameLogic.setChip(0, initialField, player).get
      val move2 = gameLogic.setChip(1, move1, player).get
      val move3 = gameLogic.setChip(2, move2, player).get
      val move4 = gameLogic.setChip(3, move3, player).get
      gameLogic.checkIfSomeoneWon(move4, player) should be (Some(true))
    }

    "return true if player won with 4 tokens vertical" in {
      val move1 = gameLogic.setChip(0, initialField, player).get
      val move2 = gameLogic.setChip(0, move1, player).get
      val move3 = gameLogic.setChip(0, move2, player).get
      val move4 = gameLogic.setChip(0, move3, player).get
      gameLogic.checkIfSomeoneWon(move4, player) should be (Some(true))
    }

    "return true if 4 tokens are vertical from position 0|0 -> 3|3"  in {
      val move1 = gameLogic.setChip(0, initialField, player).get
      val move2 = gameLogic.setChip(1, move1, player).get
      val move3 = gameLogic.setChip(1, move2, player).get
      val move4 = gameLogic.setChip(2, move3, player).get
      val move5 = gameLogic.setChip(2, move4, player).get
      val move6 = gameLogic.setChip(2, move5, player).get
      val move7 = gameLogic.setChip(3, move6, new PlayerModel("Hans Peter", 'o')).get
      val move8 = gameLogic.setChip(3, move7, player).get
      val move9 = gameLogic.setChip(3, move8, player).get
      val move10 = gameLogic.setChip(3, move9, player).get
      gameLogic.countDiagonal(6,0,0,"",0,0, move10.rows.map(_.map(_.name))) should be (3)
    }

    "return the boolean of unsuccessively tokens diagonal" in{
      val move1 = gameLogic.setChip(0, initialField, player).get
      val move2 = gameLogic.setChip(1, move1, player).get
      gameLogic.checkIfSomeoneWon(move2, player) should be (Some(false))
    }
  }
}
