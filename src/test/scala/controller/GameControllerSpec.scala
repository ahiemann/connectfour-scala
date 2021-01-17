package controller

import util.AutomaticMatchfield
import dsl.GameColumnImplicit._
import model.{GameOverFlag, MatchfieldModel, PlayerModel, RealPlayer}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import util.GameLogic
import view.Tui

import java.io.ByteArrayInputStream

class GameControllerSpec extends AnyWordSpec with Matchers {
  "The GameController" should {
    val controller = GameController(new Tui)
    val initialField = GameLogic.getInitialMatchField
    val automaticField = AutomaticMatchfield(initialField)
    val player1 = RealPlayer("Max Mustermann")
    val player2 = RealPlayer("Erika Mustermann", 'o')

    "return a initial matchfield, and two real players if game mode 2 was selected (including two invalid inputs)" in {
      val stdinString ="""Pascal
                         |Andreas""".stripMargin
      val stdin = ByteArrayInputStream(stdinString.getBytes)
      Console.withIn(stdin) {
        val result = controller.startGame()
        result._1 shouldBe a [RealPlayer]
        result._2 shouldBe a [RealPlayer]
        result._3 shouldBe a [MatchfieldModel[_]]
      }
    }

    "succesfully execute a game round with two real players (including two invalid columns)" in {
      val stdinString =
        """wrongInput
          |99
          |2""".stripMargin
      val stdin = ByteArrayInputStream(stdinString.getBytes)
      Console.withIn(stdin) {
        val result = controller.playRound(initialField, player1, player2, player1)
        val tupel = result.left.getOrElse("").asInstanceOf[(PlayerModel, MatchfieldModel[PlayerModel])]
        tupel._1.sign shouldEqual 'o'  // next player: player2
        tupel._2.cell(0, 1).sign shouldEqual 'x' // set by player1
      }
    }
    
    "return a GameOverFlag that indicates the game is over for some reason" in {
      val matchfieldWithWinner = automaticField.play(0 -> player1, 1 -> player1, 2 -> player1)
      controller.doRound(3, matchfieldWithWinner, player1, player2, player1).getOrElse(false) shouldBe a [GameOverFlag.type]
    }

    "return a Either Left with the next player and current state of the matchfield if the game is not over yet" in {
      val result = controller.doRound(0, initialField, player1, player2, player1).left.getOrElse("").asInstanceOf[(PlayerModel, MatchfieldModel[PlayerModel])]
      result._1 shouldBe a [PlayerModel]
      result._2 shouldBe a [MatchfieldModel[_]]
    }

    "end because the game is a draw" in {
      val matchfield = automaticField.play(
        0 -> player1, 0 -> player2, 0 -> player1, 0 -> player2, 0 -> player1, 0 -> player2,
        1 -> player1, 1 -> player2, 1 -> player1, 1 -> player2, 1 -> player1, 1 -> player2,
        2 -> player1, 2 -> player2, 2 -> player1, 2 -> player2, 2 -> player1, 2 -> player2,
        3 -> player2, 3 -> player1, 3 -> player2, 3 -> player1, 3 -> player2, 3 -> player1,
        4 -> player2, 4 -> player1, 4 -> player2, 4 -> player1, 4 -> player2, 4 -> player1,
        5 -> player1, 5 -> player2, 5 -> player1, 5 -> player2, 5 -> player1, 5 -> player2,
        6 -> player1, 6 -> player2, 6 -> player1, 6 -> player2, 6 -> player1
      )
      val result = controller.doRound(6, matchfield, player1, player2, player2)
      result.getOrElse(false) shouldBe a [GameOverFlag.type]
    }

    "return a Failure if there was a failure while playing a round (i.e. setting the chip and checking if the game is over)" in {
      val fullColumnField = automaticField.play(0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1)
      val result = controller.doRound(0, fullColumnField, player1, player2, player2).left.getOrElse("").asInstanceOf[(PlayerModel, MatchfieldModel[PlayerModel])]
      result._1 shouldBe a [PlayerModel]
      result._2 shouldBe a [MatchfieldModel[_]]
    }
  }
}
