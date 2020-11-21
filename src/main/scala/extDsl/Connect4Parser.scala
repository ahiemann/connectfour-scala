package extDsl

import dsl.GameColumnPlayerMapping
import scala.language.postfixOps
import model.{Connect4Model, PlayerModel}

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

  private def rounds: Parser[List[(Int, Int)]] = roundsPlayer1First ||| roundsPlayer2First

  //private def rounds: Parser[List[(Int, Int)]] = (rep(player2First) | rep(player1First)) ^^ (roundData => roundData.flatten)
  private def roundsPlayer1First: Parser[List[(Int, Int)]] =
    rep(player1First) ~ opt(player1Round | player1RoundCompact) ^^ {
      case ps ~ Some(p) => ps.flatten.appended(p)
      case ps ~ None => ps.flatten
    }

  private def roundsPlayer2First: Parser[List[(Int, Int)]] =
    rep(player2First) ~ opt(player2Round | player2RoundCompact) ^^ {
      case ps ~ Some(p) => ps.flatten.appended(p)
      case ps ~ None => ps.flatten
    }

/*
  private def roundOrder(firstPlayerRound: Parser[(Int, Int)], secondPlayerRound: Parser[(Int, Int)]) : Parser[List[(Int, Int)]] =
    firstPlayerRound ~ secondPlayerRound ^^ {
      case first ~ second => List[(Int, Int)](first, second)
    }
*/

  private def player1First : Parser[List[(Int, Int)]] =
    (player1Round | player1RoundCompact) ~ (player2Round | player2RoundCompact) ^^ {
      case p1round ~ p2round => List[(Int, Int)](p1round, p2round)
    }

  private def player2First : Parser[List[(Int, Int)]] =
    (player2Round | player2RoundCompact) ~ (player1Round | player1RoundCompact) ^^ {
      case p2round ~ p1round => List[(Int, Int)](p2round, p1round)
    }

  private def player1Round : Parser[(Int, Int)] =
    "Set" ~
      "chip" ~
      "for" ~
      "player" ~ "1" ~ "in" ~ "column" ~ columnNumber ^^ { case _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ columnNr => (1, columnNr) }

  private def player2Round : Parser[(Int, Int)] =
    "Set" ~
      "chip" ~
      "for" ~
      "player" ~ "2" ~ "in" ~ "column" ~ columnNumber ^^ { case _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ columnNr => (2, columnNr) }

  private def player1RoundCompact : Parser[(Int, Int)] =
    "p" ~ "1" ~
      ":" ~
      "c" ~ columnNumber ^^ {
      case _ ~ _ ~ _ ~ _ ~ columnNr => (1, columnNr)
    }

  private def player2RoundCompact : Parser[(Int, Int)] =
    "p" ~ "2" ~
      ":" ~
      "c" ~ columnNumber ^^ {
      case _ ~ _ ~ _ ~ _ ~ columnNr => (2, columnNr)
    }

  private def name: Parser[String] = word

  private def word: Parser[String] = """\w+""".r

  //matches one of the two available tokens
  private def symbol: Parser[Char] = """[x|o]""".r ^^ (_.charAt(0))

  private def playerNumber: Parser[Int] = """[1|2]""".r ^^ (_.toInt)

  //matches a number in the range 1-7, corresponding to the number of columns in Connect Four
  private def columnNumber: Parser[Int] = """[1-7]""".r ^^ (_.toInt)

}
