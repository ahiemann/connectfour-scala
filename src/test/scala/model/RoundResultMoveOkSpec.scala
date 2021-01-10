package model

import controllers.GameLogic
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

class RoundResultMoveOkSpec extends AnyWordSpec with should.Matchers {
  "A RoundResultMoveOk" should {
      val roundResultMoveOk = RoundResultMoveOk(GameLogic.getInitialMatchField())

      "have a matchfield" in {
        roundResultMoveOk.matchfield shouldBe a [MatchfieldModel[?]]
      }
  }
}
