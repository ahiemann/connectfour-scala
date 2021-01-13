package controller

import dsl.AutomaticMatchfieldImplicit.AutomaticMatchfield
import dsl.GameColumnImplicit.GameColumn
import model.{GameOverFlag, MatchfieldModel, PlayerModel, RealPlayer}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import util.GameLogic
import view.Tui

class GameControllerSpec extends AnyWordSpec with Matchers {
  "The GameController" should {
    val controller = new GameController(new Tui)
    val initialField = GameLogic.getInitialMatchField()
    val player1 = RealPlayer("Max Mustermann", 'x')
    val player2 = RealPlayer("Erika Mustermann", 'o')

    "return a GameOverFlag that indicates the game is over for some reason" in {
      val matchfieldWithWinner = initialField.play(0 -> player1, 1 -> player1, 2 -> player1)
      controller.doRound(3, matchfieldWithWinner, player1, player2, player1).getOrElse(false) shouldBe a [GameOverFlag.type]
    }

    "return a Either Left with the next player and current state of the matchfield if the game is not over yet" in {
      val result = controller.doRound(0, initialField, player1, player2, player1).left.getOrElse() shouldBe a [(PlayerModel, MatchfieldModel[PlayerModel])]
    }

    "return a Failure if there was a failure while playing a round (i.e. setting the chip and checking if the game is over)" in {
      val fullColumnField = initialField.play(0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1, 0 -> player1)
      controller.doRound(0, fullColumnField, player1, player2, player2).left.getOrElse() shouldBe a [(PlayerModel, MatchfieldModel[PlayerModel])]
    }
  }
}
