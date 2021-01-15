package dsl

import util.GameLogic
import model.RealPlayer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class MatchfieldMediatorSpec extends AnyWordSpec with Matchers {

  "A MatchfieldMediator instance" should {
    val player = RealPlayer("Max Mustermann", 'O')

    val matchfield = GameLogic.getInitialMatchField
    val matchfieldMediator = new MatchfieldMediator(1, player)

    "return a Try of a RoundModel object when the ofMatchfield function is called" in  {
      matchfieldMediator.ofMatchfield(matchfield) shouldBe a [Try[_]]
    }
  }
}
