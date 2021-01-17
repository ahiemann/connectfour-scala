package dsl

import model.RealPlayer
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ColumnMediatorSpec extends AnyWordSpec with Matchers {

  "A ColumnMediator instance" should {
    val player = RealPlayer("Max Mustermann", 'O')
    val columnMediator = ColumnMediator(player)
    "return a MatchFieldMediator object when the inColumn function is called" in  {
      columnMediator inColumn 1 shouldBe a [MatchfieldMediator]
    }
  }
}
