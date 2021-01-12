package model

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import util.GameLogic

class RoundResultGameOverSpec extends AnyWordSpec with should.Matchers {
  "A RoundResultGameOver" should {
      val roundResultGameOver = RoundResultGameOver(GameLogic.getInitialMatchField(), "This game is over before it has even started. ")

      "have a matchfield" in {
        roundResultGameOver.matchfield shouldBe a [MatchfieldModel[_]]
      }
      "have a string that describes why the game is over" in {
        roundResultGameOver.gameOverReason shouldBe a [String]
      }
  }
}
