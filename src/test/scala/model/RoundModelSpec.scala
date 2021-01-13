package model

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

class RoundModelSpec extends AnyWordSpec with should.Matchers {
  "A RoundModel" should {
    val player1 = RealPlayer("Max Mustermann", 'X')
    val noPlayerPlayer = RealPlayer("NoPlayer", '-')

    val matchfield = MatchfieldModel(Vector[Vector[PlayerModel]](
      Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
      Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
      Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
      Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
      Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer),
      Vector(noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer, noPlayerPlayer)
    ))

    val roundModel = RoundModel(0, matchfield, player1)
    "have a column index" in { roundModel.columnIndex should be (0) }
    "have an instance of a matchfield" in { roundModel.matchfield should === (matchfield)}
    "have an instance of a player that belongs to this round" in { roundModel.player should === (player1) }
  }
}
