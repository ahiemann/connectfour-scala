package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MatchfieldModelSpec extends AnyWordSpec with Matchers {
  "A MatchfieldModel" when {
    "empty" should {
      "be created with default size 6x7" in {
        val matchfield = new MatchfieldModel[PlayerModel](new PlayerModel("NoPlayer", '-'))
        matchfield.size should be(6)
      }
      "be created with default player name" in {
        val matchfield = new MatchfieldModel[PlayerModel](new PlayerModel("NoPlayer", '-'))
        matchfield.rows(0)(1).name should be ("NoPlayer")
      }
      "be created with default sign" in {
        val matchfield = new MatchfieldModel[PlayerModel](new PlayerModel("NoPlayer", '-'))
        matchfield.rows(0)(1).sign should be ('-')
      }
    }
    "Created with matrix" should {
      "be possible to set player name and token into first column" in {
        val matchfield = new MatchfieldModel[PlayerModel](new PlayerModel("NoPlayer", '-'))
        val m = matchfield.setToken(0,0,(new PlayerModel("Pascal", 'x')))
        m.rows(0)(0).name should be("Pascal")
        m.rows(0)(0).sign should be('x')
      }
    }
  }
}
