package model

trait PlayerModel {
  val name:String
  val sign:Char

  override def toString: String = this.sign.toString
}

case class RealPlayer(name: String = "player", sign: Char = 'x') extends PlayerModel

case class AIPlayer(override val name: String = "Computer", override val sign: Char = 'o') extends PlayerModel