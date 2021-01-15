package dsl

import dsl.GameColumnImplicit._
import model.RealPlayer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameColumnSpec extends AnyWordSpec with Matchers {

  "An GameColumn object" should {
    val player1 = RealPlayer("Max Mustermann", 'O')
    
    "return a GameColumnPlayerMapping when the pattern <Int> -> <PlayerModel> occurs" in {
      1 -> player1 shouldBe a [GameColumnPlayerMapping]
    }
  }
}
