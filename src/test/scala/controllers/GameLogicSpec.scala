package controllers

import model.{MatchfieldModel, PlayerModel}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameLogicSpec extends AnyWordSpec with Matchers {
  "The GameLogic" should {
    val gameLogic = new GameLogic()
    val initialField = gameLogic.getInitialMatchField()
    val player1 = PlayerModel("Max Mustermann", 'x')
    val player2 = PlayerModel("Erika Mustermann", 'o')
    val player3 = PlayerModel("NoPlayer", '-')




    "return an initial match field" in {
      gameLogic.getInitialMatchField().toString() should be("MatchfieldModel(Vector(Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return an Some(MatchfieldModel(...)) with one token" in {
      gameLogic.setChip(0, initialField, player1).get.toString() should be ("MatchfieldModel(Vector(Vector(x, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return the next free row for token" in {
      val column = 0
      gameLogic.getNextEmptyRow(0, column, player1, initialField.rows) should be (0)
      val move1 = gameLogic.setChip(column , initialField, player1).get
      val move2 = gameLogic.setChip(column, move1, player1).get
      gameLogic.getNextEmptyRow(0, column, player1, move2.rows) should be (2)
    }

    "return the last free row for token" in {
      val column = 0
      val move1 = gameLogic.setChip(column , initialField, player1).get
      val move2 = gameLogic.setChip(column, move1, player1).get
      val move3 = gameLogic.setChip(column, move2, player1).get
      val move4 = gameLogic.setChip(column, move3, player1).get
      val move5 = gameLogic.setChip(column, move4, player1).get
      gameLogic.getNextEmptyRow(0, column, player1, move5.rows) should be (5)
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

    "return the number of successively tokens diagonal" in{
      val move1 = gameLogic.setChip(0, initialField, player1).get
      val move2 = gameLogic.setChip(1, move1, player1).get
      val move3 = gameLogic.setChip(1, move2, player1).get
      val move4 = gameLogic.setChip(2, move3, player1).get
      val move5 = gameLogic.setChip(2, move4, player1).get
      val move6 = gameLogic.setChip(2, move5, player1).get
      val move7 = gameLogic.setChip(3, move6, new PlayerModel("Hans Peter", 'o')).get
      val move8 = gameLogic.setChip(3, move7, player1).get
      val move9 = gameLogic.setChip(3, move8, player1).get
      val move10 = gameLogic.setChip(3, move9, player1).get
      gameLogic.countDiagonal(6,0,0,"",0,0, move10.rows.map(_.map(_.name))) should be (4)
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
