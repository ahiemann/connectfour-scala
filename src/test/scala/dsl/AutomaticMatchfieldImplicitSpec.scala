package dsl

import controllers.GameLogic
import dsl.AutomaticMatchfieldImplicit.AutomaticMatchfield
import dsl.GameColumnImplicit.GameColumn
import model.{MatchfieldModel, PlayerModel}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AutomaticMatchfieldImplicitSpec extends AnyWordSpec with Matchers {

  "An AutomaticMatchfield" should {
    val player1 = PlayerModel("Max Mustermann", 'O')
    val initialMatchfield = GameLogic.getInitialMatchField()

    "convert a MatchfieldModel instance to an instance of the implicit AutomaticMatchfield class" in {
      AutomaticMatchfieldImplicit.convertMatchFieldToAutomaticMatchfield(initialMatchfield) shouldBe a [AutomaticMatchfield]
    }
    "return a matchfield when its play function is called that represents the game moves from the passed GameColumnPlayerMappings" in {
      val automaticMatchfield = AutomaticMatchfieldImplicit.convertMatchFieldToAutomaticMatchfield(initialMatchfield)
      automaticMatchfield.play(
        GameColumnPlayerMapping(1, player1),
        GameColumnPlayerMapping(2, player1),
        GameColumnPlayerMapping(3, player1)) shouldBe a [MatchfieldModel[_]]
    }
  }
}
