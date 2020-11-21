package extDsl

import dsl.GameColumnPlayerMapping
import scala.language.postfixOps
import model.{Connect4Model, PlayerModel, RoundModel}

import scala.util.parsing.combinator.RegexParsers

class Connect4Parser extends RegexParsers {

  def parseConnect4Dsl(input: String): Either[String, Connect4Model] =
    parseAll(connect4Parser, input) match {
      case Success(t,_) => Right(t)
      case NoSuccess(msg,_) =>
        Left(s"Failed parsing: $msg")
    }

  private def connect4Parser: Parser[Connect4Model] = {
    player ~
      player ~
      rounds ^^ {
      case player1 ~ player2 ~ roundData =>
        val roundDataWithRealPlayers = roundData.map(
          round => {
            val player = if (round._1 == 1) player1 else player2
            GameColumnPlayerMapping(round._2, player)
          })
        Connect4Model(player1, player2, roundDataWithRealPlayers)
    }
  }


  private def player: Parser[PlayerModel] =
    "Player" ~ playerNumber ~
      "has name" ~ name ~
      "and symbol" ~ symbol ^^ {
      case _ ~ _ ~ _ ~ n ~ _ ~ s =>
        PlayerModel(n, s)
    }

  private def rounds: Parser[List[(Int, Int)]] = rep(round | roundCompact)

  private def round: Parser[(Int, Int)] =
    "Set chip for player" ~ playerNumber ~
      "in column" ~ columnNumber ^^ {
      case _ ~ playerNr ~ _ ~ columnNr => (playerNr, columnNr)
    }

  private def roundCompact: Parser[(Int, Int)] =
    "p" ~ playerNumber ~
      ":" ~
      "c" ~ columnNumber ^^ {
      case _ ~ playerNr ~ _ ~ _ ~ columnNr => (playerNr, columnNr)
    }

  private def name: Parser[String] = word

  private def word: Parser[String] = """\w+""".r

  //matches one of the two available tokens
  private def symbol: Parser[Char] = """[x|o]""".r ^^ (_.charAt(0))

  private def playerNumber: Parser[Int] = """[1|2]""".r ^^ (_.toInt)

  //matches a number in the range 1-7, corresponding to the number of columns in Connect Four
  private def columnNumber: Parser[Int] = """[1-7]""".r ^^ (_.toInt)
}
