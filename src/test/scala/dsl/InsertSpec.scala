package dsl

import model.PlayerModel
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class InsertSpec extends AnyWordSpec with Matchers {

  "An Insert object" should {
    val player = PlayerModel("Max Mustermann", 'O')
    "return a ColumnMediator object when the chipOf function is called" in  {
      Insert chipOf player shouldBe a [ColumnMediator]
    }
  }
}
