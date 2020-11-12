package controllers

import model.{MatchfieldModel, PlayerModel}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameLogicSpec extends AnyWordSpec with Matchers {
  "The GameLogic" should {
    val gameLogic = new GameLogic()
    val initialField = gameLogic.getInitialMatchField()
    val player = PlayerModel("Max Mustermann", 'x')

    "return an initial match field" in {
      gameLogic.getInitialMatchField().toString() should be("MatchfieldModel(Vector(Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return an Some(MatchfieldModel(...)) with one token" in {
      gameLogic.setChip(0, initialField, player).get.toString() should be ("MatchfieldModel(Vector(Vector(x, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -), Vector(-, -, -, -, -, -, -)))")
    }

    "return the next free row for token" in {
      val column = 0
      gameLogic.getNextEmptyRow(0, column, player, initialField.rows) should be (0)
      val move1 = gameLogic.setChip(column , initialField, player).get
      val move2 = gameLogic.setChip(column, move1, player).get
      gameLogic.getNextEmptyRow(0, column, player, move2.rows) should be (2)
    }

    "return the last free row for token" in {
      val column = 0
      val move1 = gameLogic.setChip(column , initialField, player).get
      val move2 = gameLogic.setChip(column, move1, player).get
      val move3 = gameLogic.setChip(column, move2, player).get
      val move4 = gameLogic.setChip(column, move3, player).get
      val move5 = gameLogic.setChip(column, move4, player).get
      gameLogic.getNextEmptyRow(0, column, player, move5.rows) should be (5)
    }

    "return true if the game is draw" in {
      gameLogic.checkIfDraw(initialField) should be (false)
    }

    "return the number of successively tokens diagonal" in{
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
      gameLogic.countDiagonal(6,0,0,"",0,0, move10.rows.map(_.map(_.name))) should be (4)
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
      gameLogic.countDiagonal(6,0,0,"",0,0, move10.rows.map(_.map(_.name))) should be (4)
    }

    "return the boolean of unsuccessively tokens diagonal" in{
      val move1 = gameLogic.setChip(0, initialField, player).get
      val move2 = gameLogic.setChip(1, move1, player).get
      gameLogic.checkIfSomeoneWon(move2, player) should be (Some(false))
    }
  }
}
