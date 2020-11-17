package dsl

import model.PlayerModel
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ColumnMediatorSpec extends AnyWordSpec with Matchers {

  "A ColumnMediator instance" should {
    val player = PlayerModel("Max Mustermann", 'O')
    val columnMediator = new ColumnMediator(player)
    "return a MatchFieldMediator object when the inColumn function is called" in  {
      columnMediator inColumn 1 shouldBe a [MatchfieldMediator]
    }
  }
}
