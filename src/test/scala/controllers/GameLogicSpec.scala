package controllers

import model.{MatchfieldModel, PlayerModel, RoundModel}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameLogicSpec extends AnyWordSpec with Matchers {
  "The GameLogic" should {
    val gameLogic = new GameLogic()
    val initialField = gameLogic.getInitialMatchField()
    val player1 = PlayerModel("Max Mustermann", 'x')
    val player2 = PlayerModel("Erika Mustermann", 'o')
    val noPlayerPlayer = PlayerModel("NoPlayer", '-')
    val initialRoundModel = Right(RoundModel(0, initialField, player1)).withLeft[String]


    "return an initial match field" in {
      gameLogic.getInitialMatchField().toString should be("MatchfieldModel(Vector(Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return an Some(MatchfieldModel(...)) with one token" in {
      gameLogic.setChip(initialRoundModel).getOrElse(throw new Exception("No RoundModel found")).matchField.toString should be ("MatchfieldModel(Vector(Vector(x, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return the next free row for token" in {
      val column = 0
      gameLogic.getNextEmptyRow(column, initialField) should be (Some(0))
      val round1Result = gameLogic.setChip(initialRoundModel)
      val round2Result = gameLogic.setChip(round1Result)
      val round2Field = round2Result.getOrElse(throw new Exception("No RoundModel found")).matchField
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

      val round5matchField = round5Result.getOrElse(throw new Exception("No RoundModel found")).matchField
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
      val round1MatchField = round1Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round2Result = gameLogic.setChip(Right(RoundModel(1, round1MatchField, player1)).withLeft[String])
      val round2MatchField = round2Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round3Result = gameLogic.setChip(Right(RoundModel(1, round2MatchField, player1)).withLeft[String])
      val round3MatchField = round3Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round4Result = gameLogic.setChip(Right(RoundModel(2, round3MatchField, player1)).withLeft[String])
      val round4MatchField = round4Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round5Result = gameLogic.setChip(Right(RoundModel(2, round4MatchField, player1)).withLeft[String])
      val round5MatchField = round5Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round6Result = gameLogic.setChip(Right(RoundModel(2, round5MatchField, player1)).withLeft[String])
      val round6MatchField = round6Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round7Result = gameLogic.setChip(Right(RoundModel(3, round6MatchField, PlayerModel("Hans Peter", 'o'))).withLeft[String])
      val round7MatchField = round7Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round8Result = gameLogic.setChip(Right(RoundModel(3, round7MatchField, player1)).withLeft[String])
      val round8MatchField = round8Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round9Result = gameLogic.setChip(Right(RoundModel(3, round8MatchField, player1)).withLeft[String])
      val round9MatchField = round9Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      val round10Result = gameLogic.setChip(Right(RoundModel(3, round9MatchField, player1)).withLeft[String])
      val round10MatchField = round10Result.getOrElse(throw new Exception("No RoundModel found")).matchField

      gameLogic.countDiagonal(6,0,0,"",0,0, round10MatchField.rows.map(_.map(_.name))) should be (4)
    }

    "return true if player1 won with 4 tokens diagonal" in {}
    "return true if player1 won with 4 tokens horizontal" in {}
    "return true if player1 won with 4 tokens vertical" in {}

    "return false if player1 doesnt won with 3 tokens diagonal" in {}
    "return false if player1 doesnt won with 3 tokens horizontal" in {}
    "return false if player1 doesnt won with 3 tokens vertical" in {}


  //  "check whether the game is over" in {}
  //  "check whether the game continues" in {}
  }
}
