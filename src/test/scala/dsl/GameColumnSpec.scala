package dsl

import dsl.GameColumnImplicit.GameColumn
import model.PlayerModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameColumnSpec extends AnyWordSpec with Matchers {

  "An GameColumn object" should {
    val player1 = PlayerModel("Max Mustermann", 'O')

    "convert a integer value to an instance of the implicit GameColumn class" in {
      GameColumnImplicit.convertIntToGameColumn(1) shouldBe a [GameColumn]
    }
    "return a GameColumnPlayerMapping when the pattern <Int> -> <PlayerModel> occurs" in {
      val gameColumn = GameColumnImplicit.convertIntToGameColumn(1)
      gameColumn -> player1 shouldBe a [GameColumnPlayerMapping]
    }
  }
}
