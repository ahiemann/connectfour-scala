package controller

import util.AutomaticMatchfield
import dsl.GameColumnImplicit.GameColumn
import model.{AIPlayer, GameOverFlag, MatchfieldModel, PlayerModel, RealPlayer}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import util.GameLogic
import view.Tui

import java.io.ByteArrayInputStream

class GameControllerSpec extends AnyWordSpec with Matchers {
  "The GameController" should {
    val controller = new GameController(new Tui)
    val initialField = GameLogic.getInitialMatchField()
    val automaticField = new AutomaticMatchfield(initialField)
    val player1 = RealPlayer("Max Mustermann", 'x')
    val player2 = RealPlayer("Erika Mustermann", 'o')
    val aiPlayer = AIPlayer("AI", 'o')

    "return a initial matchfield, a real players and an AI player if game mode 1 was selected (including two invalid inputs)" in {
      val stdinString ="""
                         |1
                         |Pascal""".stripMargin
      val stdin = new ByteArrayInputStream(stdinString.getBytes)
      Console.withIn(stdin) {
        val result = controller.startGame()
        result._1 shouldBe a [RealPlayer]
        result._2 shouldBe a [AIPlayer]
        result._3 shouldBe a [MatchfieldModel[_]]
      }
    }

    "return a initial matchfield, and two real players if game mode 2 was selected (including two invalid inputs)" in {
      val stdinString ="""wrongInput
                         |3
                         |2
                         |Pascal
                         |Andreas""".stripMargin
      val stdin = new ByteArrayInputStream(stdinString.getBytes)
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
      val stdin = new ByteArrayInputStream(stdinString.getBytes)
      Console.withIn(stdin) {
        val result = controller.playRound(initialField, player1, player2, player1)
        val tupel = result.left.getOrElse().asInstanceOf[(PlayerModel, MatchfieldModel[PlayerModel])]
        tupel._1.sign shouldEqual 'o'  // next player: player2
        tupel._2.cell(0, 1).sign shouldEqual 'x' // set by player1
      }
    }
    "succesfully execute a game round with one real player who is the current player" in {
      val stdinString = "2"
      val stdin = new ByteArrayInputStream(stdinString.getBytes)
      Console.withIn(stdin) {
        val result = controller.playRound(initialField, player1, aiPlayer, player1)
        val tupel = result.left.getOrElse().asInstanceOf[(PlayerModel, MatchfieldModel[PlayerModel])]
        tupel._1.sign shouldEqual 'o'  // next player: aiPlayer
        tupel._2.cell(0, 1).sign shouldEqual 'x' // set by player1
      }
    }
    "succesfully execute a game round with one real player where it's the ai players turn" in {
      val result = controller.playRound(initialField, player1, aiPlayer, aiPlayer)
      val tupel = result.left.getOrElse().asInstanceOf[(PlayerModel, MatchfieldModel[PlayerModel])]
      tupel._1.sign shouldEqual 'x'  // next player: player1
      tupel._2.cell(0, 3).sign shouldEqual 'o' // for an empty field, column 4 (index 3) is currently always the best option
    }


    "return a GameOverFlag that indicates the game is over for some reason" in {
      val matchfieldWithWinner = automaticField.play(0 -> player1, 1 -> player1, 2 -> player1)
      controller.doRound(3, matchfieldWithWinner, player1, player2, player1).getOrElse(false) shouldBe a [GameOverFlag.type]
    }

    "return a Either Left with the next player and current state of the matchfield if the game is not over yet" in {
      val result = controller.doRound(0, initialField, player1, player2, player1).left.getOrElse() shouldBe a [(PlayerModel, MatchfieldModel[PlayerModel])]
    }

    "return a Failure if there was a failure while playing a round (i.e. setting the chip and checking if the game is over)" in {
      val fullColumnField = automaticField.play(0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1)
      controller.doRound(0, fullColumnField, player1, player2, player2).left.getOrElse() shouldBe a [(PlayerModel, MatchfieldModel[PlayerModel])]
    }
  }
}
