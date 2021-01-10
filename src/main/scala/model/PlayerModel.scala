package model

case class PlayerModel(name: String = "player", sign: Char = 'X'):
  override def toString: String = this.sign.toString

